package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.LoteInsumo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Parametrizado sobre LoteInsumo solo porque TenantAwareRepository exige un
 * tipo de entidad — la query real es un CTE propio, no un CRUD sobre esa
 * entidad (lote_insumo se llena desde Compras, que no existe todavia; este
 * modulo solo lee).
 */
interface VencimientoJpaRepository extends TenantAwareRepository<LoteInsumo, Integer> {

    /**
     * dias_anticipacion_vencim sale de configuracion_sistema del tenant
     * actual (RLS ya scopea esa tabla a 1 fila) — mismo patron de lectura
     * acotada a una sola columna de otra tabla que
     * FacturacionDianJpaRepository.buscarEstadoConexionDian en el modulo
     * restaurante.
     */
    @Query(value = "WITH lote_mas_proximo AS ("
            + "  SELECT DISTINCT ON (l.insumo_id) "
            + "    l.id AS lote_id, i.id AS insumo_id, i.codigo AS insumo_codigo, i.nombre AS insumo_nombre, "
            + "    i.categoria_insumo_id AS categoria_insumo_id, i.stock_actual AS stock_actual_insumo, "
            + "    l.numero_lote AS numero_lote, l.fecha_vencimiento AS fecha_vencimiento, "
            + "    (l.fecha_vencimiento - CURRENT_DATE) AS dias_restantes "
            + "  FROM lote_insumo l JOIN insumo i ON i.id = l.insumo_id "
            + "  WHERE l.cantidad_actual > 0 AND l.fecha_vencimiento IS NOT NULL "
            + "  ORDER BY l.insumo_id, l.fecha_vencimiento ASC"
            + "), calculado AS ("
            + "  SELECT *, CASE WHEN dias_restantes < 0 THEN 'vencido' ELSE 'proximo_a_vencer' END AS estado_calc "
            + "  FROM lote_mas_proximo "
            + "  WHERE dias_restantes < 0 "
            + "     OR dias_restantes <= (SELECT COALESCE(dias_anticipacion_vencim, 7) FROM configuracion_sistema LIMIT 1)"
            + ") "
            + "SELECT * FROM calculado "
            + "WHERE (:categoriaInsumoId IS NULL OR categoria_insumo_id = :categoriaInsumoId) "
            + "AND (:estado IS NULL OR estado_calc = :estado) "
            + "ORDER BY fecha_vencimiento ASC", nativeQuery = true)
    List<LoteVencimientoRow> listar(@Param("estado") String estado,
                                     @Param("categoriaInsumoId") Integer categoriaInsumoId);
}
