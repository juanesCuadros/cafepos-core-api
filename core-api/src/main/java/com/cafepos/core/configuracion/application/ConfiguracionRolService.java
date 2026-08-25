package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.CambioPermiso;
import com.cafepos.core.configuracion.domain.MatrizPermisosRepository;
import com.cafepos.core.configuracion.domain.MatrizRolPermisos;
import com.cafepos.core.configuracion.domain.ModuloPermisos;
import com.cafepos.core.configuracion.domain.PermisoMatrizItem;
import com.cafepos.core.configuracion.domain.RolJefeNoEditableException;
import com.cafepos.core.configuracion.domain.RolNoEncontradoException;
import com.cafepos.core.configuracion.domain.UsuarioRepository;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.seguridad.PermisoCacheService;
import com.cafepos.core.shared.seguridad.Rol;
import com.cafepos.core.shared.seguridad.RolRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConfiguracionRolService {

    private final RolRepository rolRepository;
    private final MatrizPermisosRepository matrizPermisosRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisoCacheService permisoCacheService;

    public ConfiguracionRolService(RolRepository rolRepository, MatrizPermisosRepository matrizPermisosRepository,
                                    UsuarioRepository usuarioRepository, PermisoCacheService permisoCacheService) {
        this.rolRepository = rolRepository;
        this.matrizPermisosRepository = matrizPermisosRepository;
        this.usuarioRepository = usuarioRepository;
        this.permisoCacheService = permisoCacheService;
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    /**
     * Rol Jefe (es_editable=false): todo permiso se muestra activo=true
     * aunque no exista fila en rol_permiso — el bypass de codigo en
     * PermisoEvaluator es la fuente de verdad, no los datos.
     */
    @Transactional(readOnly = true)
    public MatrizRolPermisos matrizDe(Integer rolId) {
        Rol rol = rolRepository.findById(rolId).orElseThrow(RolNoEncontradoException::new);
        List<PermisoMatrizItem> crudos = matrizPermisosRepository.obtenerMatrizCruda(rolId);
        Map<String, List<PermisoMatrizItem>> porModuloPadre = new LinkedHashMap<>();
        for (PermisoMatrizItem item : crudos) {
            boolean activoFinal = !rol.isEsEditable() || item.activo();
            PermisoMatrizItem itemFinal = new PermisoMatrizItem(item.permisoId(), item.modulo(), item.accion(),
                    activoFinal);
            String padre = moduloPadre(item.modulo());
            porModuloPadre.computeIfAbsent(padre, k -> new ArrayList<>()).add(itemFinal);
        }
        List<ModuloPermisos> modulos = porModuloPadre.entrySet().stream()
                .map(e -> new ModuloPermisos(nombreDisplay(e.getKey()), e.getValue()))
                .toList();
        return new MatrizRolPermisos(rol.getNombre(), rol.isEsEditable(), modulos);
    }

    /**
     * Jefe nunca es editable, 403 siempre, antes de tocar nada del request.
     * Invalida la cache SIEMPRE al final (aunque cambios este vacio) para
     * mantener el contrato simple — un PATCH con lista vacia es un no-op
     * legitimo, no un error.
     *
     * Sin PIN de step-up hoy (no hay PinStepUpService.validar en
     * RolController) — usuario_autoriza_id en evento_auditoria queda NULL
     * para esta accion, no es un bug de este cambio.
     *
     * datos_antes/datos_despues via registrarDespues (no el valor de
     * retorno, que es solo un int) — captura la matriz REAL de los
     * permisos que cambiaron (modulo/accion/activo antes y despues), el
     * caso de uso mas valioso de @Auditable hasta ahora: quien le dio que
     * permiso a quien.
     */
    @Transactional
    @Auditable(entidadTipo = "rol_permiso", accion = "actualizar_permisos", entidadIdExpression = "#rolId")
    public int actualizarPermisos(Integer rolId, List<CambioPermiso> cambios) {
        Rol rol = rolRepository.findById(rolId).orElseThrow(RolNoEncontradoException::new);
        if (!rol.isEsEditable()) {
            throw new RolJefeNoEditableException();
        }

        Set<Integer> permisoIdsCambiados = cambios.stream().map(CambioPermiso::permisoId).collect(Collectors.toSet());
        AuditoriaContext.registrarAntes(permisosCambiados(rolId, permisoIdsCambiados));

        Integer tenantId = TenantContext.getCurrentTenantId();
        for (CambioPermiso cambio : cambios) {
            if (cambio.activo()) {
                matrizPermisosRepository.activar(tenantId, rolId, cambio.permisoId());
            } else {
                matrizPermisosRepository.desactivar(tenantId, rolId, cambio.permisoId());
            }
        }
        permisoCacheService.invalidar(tenantId, rolId);

        AuditoriaContext.registrarDespues(permisosCambiados(rolId, permisoIdsCambiados));
        return (int) usuarioRepository.contarActivosPorRol(rolId);
    }

    private List<PermisoMatrizItem> permisosCambiados(Integer rolId, Set<Integer> permisoIdsCambiados) {
        return matrizPermisosRepository.obtenerMatrizCruda(rolId).stream()
                .filter(item -> permisoIdsCambiados.contains(item.permisoId()))
                .toList();
    }

    private static String moduloPadre(String modulo) {
        int puntoIdx = modulo.indexOf('.');
        return puntoIdx >= 0 ? modulo.substring(0, puntoIdx) : modulo;
    }

    private static String nombreDisplay(String moduloPadre) {
        String[] palabras = moduloPadre.split("_");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.isEmpty()) {
                continue;
            }
            if (resultado.length() > 0) {
                resultado.append(' ');
            }
            resultado.append(Character.toUpperCase(palabra.charAt(0))).append(palabra.substring(1));
        }
        return resultado.toString();
    }
}
