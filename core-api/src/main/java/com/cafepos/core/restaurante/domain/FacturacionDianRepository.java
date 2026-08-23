package com.cafepos.core.restaurante.domain;

import java.util.Optional;

/** Puerto de persistencia de FacturacionDianResolucion — implementado en infrastructure.persistence. */
public interface FacturacionDianRepository {

    /** facturacion_dian_resolucion es 1-a-N por tenant (historico) — esta es la mas reciente. */
    Optional<FacturacionDianResolucion> buscarVigente();

    /**
     * estado_conexion_dian vive en configuracion_sistema, no en
     * facturacion_dian_resolucion — ver Javadoc de FacturacionDianEstado.
     */
    Optional<String> buscarEstadoConexion();
}
