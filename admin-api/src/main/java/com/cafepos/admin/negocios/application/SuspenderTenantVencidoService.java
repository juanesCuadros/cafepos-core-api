package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.SuscripcionesHistorial;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bean separado de VencimientoPruebaService a proposito: REQUIRES_NEW exige
 * una invocacion real a traves del proxy de Spring, no funciona si se llama
 * desde un metodo de la misma clase (self-invocation). Una transaccion por
 * tenant — si uno falla, el resto ya committeado no se deshace.
 */
@Service
public class SuspenderTenantVencidoService {

    private static final String MOTIVO_VENCIMIENTO = "Prueba vencida sin conversion a plan de pago";

    private final TenantRepository tenantRepository;
    private final SuscripcionesHistorialRepository suscripcionesHistorialRepository;

    public SuspenderTenantVencidoService(TenantRepository tenantRepository,
                                          SuscripcionesHistorialRepository suscripcionesHistorialRepository) {
        this.tenantRepository = tenantRepository;
        this.suscripcionesHistorialRepository = suscripcionesHistorialRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void suspender(Integer tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant " + tenantId + " no existe"));
        tenant.suspenderPorPruebaVencida();
        suscripcionesHistorialRepository.save(new SuscripcionesHistorial(
                tenantId, Tenant.ESTADO_PRUEBA, Tenant.ESTADO_SUSPENDIDO, null, MOTIVO_VENCIMIENTO));
    }
}
