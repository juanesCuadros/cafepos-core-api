package com.cafepos.admin.negocios;

import com.cafepos.admin.auditoria.application.AuditoriaAdminService;
import com.cafepos.admin.negocios.application.GestionTenantService;
import com.cafepos.admin.negocios.domain.OperacionTenantInvalidaException;
import com.cafepos.admin.negocios.domain.PlanNoExisteException;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorial;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GestionTenantServiceTest {

    @Mock private TenantRepository tenantRepository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private PlanRepository planRepository;
    @Mock private SuscripcionesHistorialRepository suscripcionesHistorialRepository;
    @Mock private AuditoriaAdminService auditoriaService;

    @InjectMocks
    private GestionTenantService service;

    @Test
    void suspender_tenantActivo_cambiaASuspendidoYRegistraHistorial() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_ACTIVO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        service.suspender(10, "Mora en el pago", 1);

        assertEquals(Tenant.ESTADO_SUSPENDIDO, tenant.getEstado());
        verify(tenantRepository).save(tenant);
        verify(suscripcionesHistorialRepository).save(any(SuscripcionesHistorial.class));
        verify(auditoriaService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void suspender_tenantYaSuspendido_lanzaOperacionTenantInvalidaException() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_SUSPENDIDO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        assertThrows(OperacionTenantInvalidaException.class, () ->
                service.suspender(10, "Intento suspender de nuevo", 1));
    }

    @Test
    void reactivar_tenantSuspendido_cambiaAActivoYActualizaFecha() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_SUSPENDIDO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        LocalDate nuevaFecha = LocalDate.of(2026, 10, 15);
        service.reactivar(10, nuevaFecha, "Pago confirmado", 1);

        assertEquals(Tenant.ESTADO_ACTIVO, tenant.getEstado());
        assertEquals(nuevaFecha, tenant.getFechaProximaFacturacion());
        verify(tenantRepository).save(tenant);
        verify(suscripcionesHistorialRepository).save(any(SuscripcionesHistorial.class));
    }

    @Test
    void cancelar_tenant_cambiaACancelado() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_ACTIVO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        service.cancelar(10, "Cierre definitivo del negocio", 1);

        assertEquals(Tenant.ESTADO_CANCELADO, tenant.getEstado());
        verify(tenantRepository).save(tenant);
        verify(suscripcionesHistorialRepository).save(any(SuscripcionesHistorial.class));
    }

    @Test
    void extenderPrueba_tenantEnPrueba_actualizaFechaFacturacion() {
        LocalDate fechaOriginal = LocalDate.of(2026, 9, 10);
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_PRUEBA, fechaOriginal);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        service.extenderPrueba(10, 7, "Cortesía comercial", 1);

        assertEquals(LocalDate.of(2026, 9, 17), tenant.getFechaProximaFacturacion());
        verify(tenantRepository).save(tenant);
        verify(suscripcionesHistorialRepository).save(any(SuscripcionesHistorial.class));
    }

    @Test
    void extenderPrueba_tenantNoEnPrueba_lanzaOperacionTenantInvalidaException() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_ACTIVO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));

        assertThrows(OperacionTenantInvalidaException.class, () ->
                service.extenderPrueba(10, 5, "No permitido en activo", 1));
    }

    @Test
    void cambiarPlan_planValido_actualizaPlanIdYRegistraHistorial() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_ACTIVO, null);
        Plan planNuevo = new Plan("Enterprise", "Desc", BigDecimal.valueOf(199000), 20, 0);

        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));
        when(planRepository.findById(2)).thenReturn(Optional.of(planNuevo));

        service.cambiarPlan(10, 2, "Upgrade a Enterprise", 1);

        verify(tenantRepository).save(tenant);
        verify(suscripcionesHistorialRepository).save(any(SuscripcionesHistorial.class));
        verify(auditoriaService).registrar(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void cambiarPlan_planInexistente_lanzaPlanNoExisteException() {
        Tenant tenant = new Tenant(1, 1, "cafe-roma", Tenant.ESTADO_ACTIVO, null);
        when(tenantRepository.findById(10)).thenReturn(Optional.of(tenant));
        when(planRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(PlanNoExisteException.class, () ->
                service.cambiarPlan(10, 999, "Intento plan invalido", 1));
    }
}
