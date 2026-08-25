package com.cafepos.core.inventario.domain;

import java.util.Optional;

/** Puerto de persistencia de LoteInsumo (propio de este modulo) — implementado en infrastructure.persistence. */
public interface LoteInsumoRepository {

    LoteInsumo guardar(LoteInsumo lote);

    /** Relacion 1-a-1: cada compra_detalle genera exactamente un lote (ver LoteInsumoService.crear). */
    Optional<LoteInsumo> buscarPorCompraDetalleId(Integer compraDetalleId);
}
