package com.cafepos.admin.negocios.domain;

import java.util.Optional;

public interface FacturacionDianResolucionRepository {

    /** Mas reciente por tenant — facturacion_dian_resolucion es historico 1-a-N por tenant. */
    Optional<FacturacionDianResolucion> buscarVigentePorTenant(Integer tenantId);

    FacturacionDianResolucion guardar(FacturacionDianResolucion resolucion);
}
