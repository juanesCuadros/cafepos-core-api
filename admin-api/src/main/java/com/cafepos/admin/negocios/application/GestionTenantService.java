package com.cafepos.admin.negocios.application;

import com.cafepos.admin.auditoria.application.AuditoriaAdminService;
import com.cafepos.admin.negocios.domain.OperacionTenantInvalidaException;
import com.cafepos.admin.negocios.domain.PlanNoExisteException;
import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorial;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantNoEncontradoException;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.negocios.infrastructure.web.EditarNegocioRequest;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class GestionTenantService {

    private final TenantRepository tenantRepository;
    private final RestauranteRepository restauranteRepository;
    private final PlanRepository planRepository;
    private final SuscripcionesHistorialRepository suscripcionesHistorialRepository;
    private final AuditoriaAdminService auditoriaService;

    public GestionTenantService(TenantRepository tenantRepository,
                                RestauranteRepository restauranteRepository,
                                PlanRepository planRepository,
                                SuscripcionesHistorialRepository suscripcionesHistorialRepository,
                                AuditoriaAdminService auditoriaService) {
        this.tenantRepository = tenantRepository;
        this.restauranteRepository = restauranteRepository;
        this.planRepository = planRepository;
        this.suscripcionesHistorialRepository = suscripcionesHistorialRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public void suspender(Integer tenantId, String motivo, Integer superadminId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        String estadoAnterior = tenant.getEstado();
        if (Tenant.ESTADO_SUSPENDIDO.equals(estadoAnterior)) {
            throw new OperacionTenantInvalidaException("El negocio ya se encuentra suspendido");
        }

        tenant.suspender();
        tenantRepository.save(tenant);

        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, estadoAnterior, Tenant.ESTADO_SUSPENDIDO, superadminId, motivo));

        auditoriaService.registrar(superadminId, "suspender_tenant", "tenant", tenantId,
                estadoAnterior, Tenant.ESTADO_SUSPENDIDO, null, null);
    }

    @Transactional
    public void reactivar(Integer tenantId, LocalDate proximaFacturacion, String motivo, Integer superadminId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        String estadoAnterior = tenant.getEstado();
        LocalDate fechaFacturacion = proximaFacturacion != null
                ? proximaFacturacion
                : LocalDate.now(ZoneOffset.UTC).plusDays(30);

        tenant.reactivar(fechaFacturacion);
        tenantRepository.save(tenant);

        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, estadoAnterior, Tenant.ESTADO_ACTIVO, superadminId, motivo));

        auditoriaService.registrar(superadminId, "reactivar_tenant", "tenant", tenantId,
                estadoAnterior, Tenant.ESTADO_ACTIVO, null, null);
    }

    @Transactional
    public void cancelar(Integer tenantId, String motivo, Integer superadminId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        String estadoAnterior = tenant.getEstado();
        if (Tenant.ESTADO_CANCELADO.equals(estadoAnterior)) {
            throw new OperacionTenantInvalidaException("El negocio ya se encuentra cancelado");
        }

        tenant.cancelar();
        tenantRepository.save(tenant);

        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, estadoAnterior, Tenant.ESTADO_CANCELADO, superadminId, motivo));

        auditoriaService.registrar(superadminId, "cancelar_tenant", "tenant", tenantId,
                estadoAnterior, Tenant.ESTADO_CANCELADO, null, null);
    }

    @Transactional
    public void extenderPrueba(Integer tenantId, int diasAdicionales, String motivo, Integer superadminId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        if (!Tenant.ESTADO_PRUEBA.equals(tenant.getEstado())) {
            throw new OperacionTenantInvalidaException("Solo se puede extender la prueba a negocios en estado 'prueba'");
        }

        LocalDate fechaBase = tenant.getFechaProximaFacturacion() != null
                ? tenant.getFechaProximaFacturacion()
                : LocalDate.now(ZoneOffset.UTC);

        LocalDate nuevaFecha = fechaBase.plusDays(diasAdicionales);
        tenant.extenderPrueba(nuevaFecha);
        tenantRepository.save(tenant);

        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, Tenant.ESTADO_PRUEBA, Tenant.ESTADO_PRUEBA, superadminId,
                "Extension de prueba por " + diasAdicionales + " dias: " + motivo));

        auditoriaService.registrar(superadminId, "extender_prueba", "tenant", tenantId,
                fechaBase.toString(), nuevaFecha.toString(), null, null);
    }

    @Transactional
    public void cambiarPlan(Integer tenantId, Integer nuevoPlanId, String motivo, Integer superadminId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        Plan nuevoPlan = planRepository.findById(nuevoPlanId)
                .orElseThrow(PlanNoExisteException::new);

        Integer planAnteriorId = tenant.getPlanId();
        tenant.cambiarPlan(nuevoPlan.getId());
        tenantRepository.save(tenant);

        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, tenant.getEstado(), tenant.getEstado(), planAnteriorId, nuevoPlanId, superadminId, motivo));

        auditoriaService.registrar(superadminId, "cambiar_plan", "tenant", tenantId,
                "plan_id:" + planAnteriorId, "plan_id:" + nuevoPlanId, null, null);
    }

    @Transactional
    public void editarRestaurante(Integer tenantId, EditarNegocioRequest request, Integer superadminId) {
        tenantRepository.findById(tenantId).orElseThrow(TenantNoEncontradoException::new);

        Restaurante restaurante = restauranteRepository.findByTenantId(tenantId)
                .orElseGet(() -> new Restaurante(tenantId, request.nombreNegocio()));

        restaurante.actualizarInfo(
                request.nombreNegocio(),
                request.nit(),
                request.direccion(),
                request.departamento(),
                request.ciudad(),
                request.telefono(),
                request.correo()
        );
        restauranteRepository.save(restaurante);

        auditoriaService.registrar(superadminId, "editar_negocio", "restaurante", tenantId,
                null, request.nombreNegocio(), null, null);
    }
}
