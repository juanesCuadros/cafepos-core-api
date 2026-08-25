package com.cafepos.core.compras.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de CompraDetalle — implementado en infrastructure.persistence. */
public interface CompraDetalleRepository {

    CompraDetalle guardar(CompraDetalle detalle);

    /** Entidades crudas — usado por CompraService.anular (necesita insumoId/cantidad/costoUnitario, no el join). */
    List<CompraDetalle> listarPorCompraId(Integer compraId);

    /** Con insumo_codigo/insumo_nombre/unidad_medida ya aplanados — usado por GET /compras/{id}. */
    List<CompraDetalleItemVista> listarVistaPorCompraId(Integer compraId);

    /**
     * costo_unitario de la compra_detalle MAS RECIENTE (por compra.fecha) de
     * ese insumo, en una compra que NO este anulada, EXCLUYENDO
     * compraIdExcluir (la que se esta anulando ahora mismo) — ver
     * DECISIONES YA TOMADAS de la conversacion Compras (reversion de
     * costo_actual al anular).
     */
    Optional<BigDecimal> buscarCostoUnitarioMasRecientePorInsumo(Integer insumoId, Integer compraIdExcluir);
}
