package com.cafepos.core.caja.domain;

import java.util.Optional;

/** Puerto de persistencia de FacturaDian (propia de este modulo) — implementado en infrastructure.persistence. */
public interface FacturaDianRepository {

    FacturaDian guardar(FacturaDian factura);

    Optional<FacturaDian> buscarPorVentaId(Integer ventaId);
}
