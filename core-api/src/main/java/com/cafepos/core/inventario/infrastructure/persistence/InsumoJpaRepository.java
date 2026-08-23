package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface InsumoJpaRepository extends TenantAwareRepository<Insumo, Integer> {

    /**
     * estado_stock y valor_total se calculan en la subquery para poder
     * filtrar por estado_stock en el WHERE de afuera sin repetir el CASE
     * (Postgres no permite referenciar un alias del SELECT en su propio
     * WHERE). RLS filtra insumo y categoria_insumo por tenant_id
     * automaticamente (ver TenantAwareRepository).
     */
    @Query(value = "SELECT * FROM ("
            + "  SELECT i.id AS id, i.codigo AS codigo, i.nombre AS nombre, "
            + "         ci.id AS categoria_insumo_id, ci.nombre AS categoria_insumo_nombre, "
            + "         i.unidad_medida AS unidad_medida, i.stock_actual AS stock_actual, "
            + "         i.stock_minimo AS stock_minimo, i.costo_actual AS costo_actual, "
            + "         (i.stock_actual * i.costo_actual) AS valor_total, "
            + "         CASE WHEN i.stock_actual <= 0 THEN 'agotado' "
            + "              WHEN i.stock_actual < COALESCE(i.stock_minimo, 0) THEN 'bajo_minimo' "
            + "              ELSE 'normal' END AS estado_stock, "
            + "         i.estado AS estado, i.fecha_registro AS fecha_registro "
            + "  FROM insumo i JOIN categoria_insumo ci ON ci.id = i.categoria_insumo_id"
            + ") sub "
            + "WHERE (:categoriaInsumoId IS NULL OR sub.categoria_insumo_id = :categoriaInsumoId) "
            + "AND (:estado IS NULL OR sub.estado = :estado) "
            + "AND (:estadoStock IS NULL OR sub.estado_stock = :estadoStock) "
            + "AND (:q IS NULL OR sub.nombre ILIKE '%' || :q || '%') "
            + "ORDER BY sub.nombre", nativeQuery = true)
    List<InsumoResumenRow> listar(@Param("categoriaInsumoId") Integer categoriaInsumoId,
                                   @Param("estado") String estado,
                                   @Param("estadoStock") String estadoStock,
                                   @Param("q") String q);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM movimiento_inventario WHERE insumo_id = :insumoId)",
            nativeQuery = true)
    boolean tieneMovimientosAsociados(@Param("insumoId") Integer insumoId);
}
