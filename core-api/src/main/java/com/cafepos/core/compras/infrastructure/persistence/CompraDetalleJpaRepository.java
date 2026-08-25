package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.CompraDetalle;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

interface CompraDetalleJpaRepository extends TenantAwareRepository<CompraDetalle, Integer> {

    List<CompraDetalle> findByCompraId(Integer compraId);

    @Query(value = "SELECT cd.id AS id, cd.insumo_id AS insumo_id, i.codigo AS insumo_codigo, "
            + "i.nombre AS insumo_nombre, i.unidad_medida AS unidad_medida, cd.cantidad AS cantidad, "
            + "cd.costo_unitario AS costo_unitario, cd.numero_lote AS numero_lote, "
            + "cd.fecha_vencimiento AS fecha_vencimiento, cd.subtotal AS subtotal "
            + "FROM compra_detalle cd JOIN insumo i ON i.id = cd.insumo_id "
            + "WHERE cd.compra_id = :compraId ORDER BY cd.id", nativeQuery = true)
    List<CompraDetalleItemVistaRow> listarVistaPorCompraId(@Param("compraId") Integer compraId);

    /**
     * "Mas reciente" = mayor compra.fecha, tiebreak por compra_detalle.id
     * DESC — ver DECISIONES YA TOMADAS de la conversacion Compras
     * (reversion de costo_actual al anular). Devuelve lista (no Optional)
     * a proposito para evitar cualquier ambiguedad de mapeo Optional+
     * escalar en query nativa — el adapter toma el primero.
     */
    @Query(value = "SELECT cd.costo_unitario FROM compra_detalle cd JOIN compra c ON c.id = cd.compra_id "
            + "WHERE cd.insumo_id = :insumoId AND c.estado != 'anulada' AND c.id != :compraIdExcluir "
            + "ORDER BY c.fecha DESC, cd.id DESC LIMIT 1", nativeQuery = true)
    List<BigDecimal> buscarCostoUnitarioMasRecientePorInsumo(@Param("insumoId") Integer insumoId,
                                                              @Param("compraIdExcluir") Integer compraIdExcluir);
}
