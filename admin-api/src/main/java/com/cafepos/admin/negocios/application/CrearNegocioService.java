package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.ConfiguracionSistema;
import com.cafepos.admin.negocios.domain.ConfiguracionSistemaRepository;
import com.cafepos.admin.negocios.domain.MetodoPago;
import com.cafepos.admin.negocios.domain.MetodoPagoRepository;
import com.cafepos.admin.negocios.domain.Permiso;
import com.cafepos.admin.negocios.domain.PermisoRepository;
import com.cafepos.admin.negocios.domain.PlanNoExisteException;
import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.Rol;
import com.cafepos.admin.negocios.domain.RolPermiso;
import com.cafepos.admin.negocios.domain.RolPermisoRepository;
import com.cafepos.admin.negocios.domain.RolRepository;
import com.cafepos.admin.negocios.domain.SlugYaExisteException;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorial;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantPermisoConfig;
import com.cafepos.admin.negocios.domain.TenantPermisoConfigRepository;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.negocios.domain.TenantRolConfig;
import com.cafepos.admin.negocios.domain.TenantRolConfigRepository;
import com.cafepos.admin.negocios.domain.Usuario;
import com.cafepos.admin.negocios.domain.UsuarioRepository;
import com.cafepos.admin.negocios.infrastructure.TemporaryPasswordGenerator;
import com.cafepos.admin.negocios.infrastructure.persistence.MatrizPermisosDefaultLoader;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CrearNegocioService {

    // 8h (duracion de un turno) — 1440 (24h) equivalia a desactivar el chequeo
    // de inactividad de RefreshTokenService (core-api), nunca dispara antes de
    // que el refresh_token expire solo por su propio TTL. Ver V21 en
    // core-api/db/migration para el mismo ajuste sobre tenants ya creados.
    private static final int MINUTOS_INACTIVIDAD_DEFAULT = 480;

    /** Ver PENDIENTE.md o el prompt original: lista fija acordada de acciones que exigen PIN de step-up. */
    private static final List<String[]> PERMISOS_CON_PIN = List.of(
            new String[]{"caja.pos", "eliminar_item_preparado"},
            new String[]{"caja.apertura_cierre", "registrar_egreso"},
            new String[]{"caja.historial_ventas", "anular"},
            new String[]{"caja.facturacion", "generar_nota_credito"},
            new String[]{"caja.devoluciones", "solicitar"},
            new String[]{"caja.devoluciones", "autorizar"},
            new String[]{"inventario.existencias", "ajustar"},
            new String[]{"compras.historial_compras", "eliminar"}
    );

    private final TenantRepository tenantRepository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final TenantPermisoConfigRepository tenantPermisoConfigRepository;
    private final TenantRolConfigRepository tenantRolConfigRepository;
    private final ConfiguracionSistemaRepository configuracionSistemaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final SuscripcionesHistorialRepository suscripcionesHistorialRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final MatrizPermisosDefaultLoader matrizPermisosDefaultLoader;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    public CrearNegocioService(TenantRepository tenantRepository,
                                RestauranteRepository restauranteRepository,
                                UsuarioRepository usuarioRepository,
                                RolRepository rolRepository,
                                PermisoRepository permisoRepository,
                                RolPermisoRepository rolPermisoRepository,
                                TenantPermisoConfigRepository tenantPermisoConfigRepository,
                                TenantRolConfigRepository tenantRolConfigRepository,
                                ConfiguracionSistemaRepository configuracionSistemaRepository,
                                MetodoPagoRepository metodoPagoRepository,
                                SuscripcionesHistorialRepository suscripcionesHistorialRepository,
                                PlanRepository planRepository,
                                PasswordEncoder passwordEncoder,
                                MatrizPermisosDefaultLoader matrizPermisosDefaultLoader,
                                TemporaryPasswordGenerator temporaryPasswordGenerator) {
        this.tenantRepository = tenantRepository;
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.tenantPermisoConfigRepository = tenantPermisoConfigRepository;
        this.tenantRolConfigRepository = tenantRolConfigRepository;
        this.configuracionSistemaRepository = configuracionSistemaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.suscripcionesHistorialRepository = suscripcionesHistorialRepository;
        this.planRepository = planRepository;
        this.passwordEncoder = passwordEncoder;
        this.matrizPermisosDefaultLoader = matrizPermisosDefaultLoader;
        this.temporaryPasswordGenerator = temporaryPasswordGenerator;
    }

    @Transactional
    public NegocioCreado ejecutar(String nombreNegocio, String slug, Integer planId,
                                   String correoJefe, String nombreJefe, Integer superadminId) {
        Plan plan = planRepository.findById(planId).orElseThrow(PlanNoExisteException::new);
        if (tenantRepository.existsBySlug(slug)) {
            throw new SlugYaExisteException();
        }

        String estadoInicial;
        LocalDate fechaProximaFacturacion;
        String motivo;
        if (plan.getDiasPrueba() > 0) {
            estadoInicial = Tenant.ESTADO_PRUEBA;
            fechaProximaFacturacion = LocalDate.now().plusDays(plan.getDiasPrueba());
            motivo = "Alta de negocio nuevo - periodo de prueba";
        } else {
            estadoInicial = Tenant.ESTADO_ACTIVO;
            fechaProximaFacturacion = null;
            motivo = "Alta de negocio nuevo - activacion directa";
        }

        // a. tenants
        Tenant tenant = tenantRepository.save(
                new Tenant(superadminId, planId, slug, estadoInicial, fechaProximaFacturacion));
        Integer tenantId = tenant.getId();

        // b. restaurantes (Eliminado: ahora se provisiona automáticamente por un trigger en DB tras crear el tenant)

        List<Rol> roles = rolRepository.findAll();
        Map<String, Integer> rolIdPorNombre = roles.stream()
                .collect(Collectors.toMap(Rol::getNombre, Rol::getId));
        Integer rolJefeId = rolIdPorNombre.get("Jefe");

        // c. usuario (Jefe)
        String passwordTemporal = temporaryPasswordGenerator.generar();
        usuarioRepository.save(new Usuario(tenantId, rolJefeId, nombreJefe, correoJefe,
                passwordEncoder.encode(passwordTemporal)));

        Map<String, Integer> permisoIdPorClave = permisoRepository.findAll().stream()
                .collect(Collectors.toMap(p -> p.getModulo() + "|" + p.getAccion(), Permiso::getId));

        // d. rol_permiso — solo las combinaciones activo=true de la matriz por defecto
        construirRolPermisos(tenantId, rolIdPorNombre, permisoIdPorClave).forEach(rolPermisoRepository::save);

        // e. tenant_permiso_config — solo los permisos que exigen PIN
        construirTenantPermisoConfigs(tenantId, permisoIdPorClave).forEach(tenantPermisoConfigRepository::save);

        // f. tenant_rol_config (Eliminado: se provisiona por trigger de base de datos)
        
        // g. configuracion_sistema (Eliminado: se provisiona por trigger de base de datos)

        // h. metodo_pago
        metodoPagoRepository.save(new MetodoPago(tenantId, "Efectivo", true, "activo"));

        // i. suscripciones_historial
        suscripcionesHistorialRepository.save(
                new SuscripcionesHistorial(tenantId, null, estadoInicial, superadminId, motivo));

        return new NegocioCreado(tenantId, slug, correoJefe, passwordTemporal);
    }

    private List<RolPermiso> construirRolPermisos(Integer tenantId, Map<String, Integer> rolIdPorNombre,
                                                    Map<String, Integer> permisoIdPorClave) {
        List<RolPermiso> resultado = new ArrayList<>();
        for (MatrizPermisosDefaultLoader.Fila fila : matrizPermisosDefaultLoader.filas()) {
            Integer permisoId = permisoIdPorClave.get(fila.clave());
            if (permisoId == null) {
                continue;
            }
            agregarSiActivo(resultado, tenantId, rolIdPorNombre.get("Jefe"), permisoId, fila.jefe());
            agregarSiActivo(resultado, tenantId, rolIdPorNombre.get("Admin"), permisoId, fila.admin());
            agregarSiActivo(resultado, tenantId, rolIdPorNombre.get("Cajero"), permisoId, fila.cajero());
            agregarSiActivo(resultado, tenantId, rolIdPorNombre.get("Mesero"), permisoId, fila.mesero());
            agregarSiActivo(resultado, tenantId, rolIdPorNombre.get("Cocina"), permisoId, fila.cocina());
        }
        return resultado;
    }

    private void agregarSiActivo(List<RolPermiso> lista, Integer tenantId, Integer rolId, Integer permisoId, boolean activo) {
        if (activo) {
            lista.add(new RolPermiso(tenantId, rolId, permisoId));
        }
    }

    private List<TenantPermisoConfig> construirTenantPermisoConfigs(Integer tenantId, Map<String, Integer> permisoIdPorClave) {
        return PERMISOS_CON_PIN.stream()
                .map(mp -> permisoIdPorClave.get(mp[0] + "|" + mp[1]))
                .filter(java.util.Objects::nonNull)
                .map(permisoId -> new TenantPermisoConfig(tenantId, permisoId, true))
                .toList();
    }
}
