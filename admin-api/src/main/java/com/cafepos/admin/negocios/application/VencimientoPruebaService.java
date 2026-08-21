package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta la suspension de tenants con prueba vencida. La llaman dos
 * caminos distintos: VencimientoPruebaJob (@Scheduled, medianoche) y
 * POST /admin/negocios/vencimiento-prueba (manual — pruebas en dev, o
 * reintento operativo si el job automatico fallo una noche).
 */
@Service
public class VencimientoPruebaService {

    private static final Logger log = LoggerFactory.getLogger(VencimientoPruebaService.class);

    private final TenantRepository tenantRepository;
    private final SuspenderTenantVencidoService suspenderTenantVencidoService;

    public VencimientoPruebaService(TenantRepository tenantRepository,
                                     SuspenderTenantVencidoService suspenderTenantVencidoService) {
        this.tenantRepository = tenantRepository;
        this.suspenderTenantVencidoService = suspenderTenantVencidoService;
    }

    /**
     * UTC explicito, no LocalDate.now() ambiental: el driver JDBC de
     * Postgres alinea el timezone de la sesion con el default de la JVM al
     * conectar, asi que un CURRENT_DATE del lado de la base termina siendo
     * tan ambiguo como el reloj del cliente. UTC es el criterio
     * determinista, consistente con como se guardan el resto de los
     * timestamps del sistema (TIMESTAMPTZ).
     */
    public VencimientoPruebaResultado ejecutar() {
        LocalDate hoyUtc = LocalDate.now(ZoneOffset.UTC);
        List<Tenant> vencidos = tenantRepository.findByEstadoAndFechaProximaFacturacionBefore(Tenant.ESTADO_PRUEBA, hoyUtc);

        List<String> slugsSuspendidos = new ArrayList<>();
        for (Tenant tenant : vencidos) {
            try {
                suspenderTenantVencidoService.suspender(tenant.getId());
                slugsSuspendidos.add(tenant.getSlug());
            } catch (RuntimeException e) {
                log.error("No se pudo suspender el tenant {} por prueba vencida", tenant.getId(), e);
            }
        }
        log.info("Vencimiento de prueba: {} de {} tenants candidatos quedaron suspendidos",
                slugsSuspendidos.size(), vencidos.size());

        return new VencimientoPruebaResultado(vencidos.size(), slugsSuspendidos.size(), slugsSuspendidos);
    }
}
