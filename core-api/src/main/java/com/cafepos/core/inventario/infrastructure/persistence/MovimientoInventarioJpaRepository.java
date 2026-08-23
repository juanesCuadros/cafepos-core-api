package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

interface MovimientoInventarioJpaRepository extends TenantAwareRepository<MovimientoInventario, Integer> {

    /** LEFT JOIN insumo/usuario — insumo_id y usuario_id son nullable en la tabla (el CHECK solo exige insumo XOR producto). */
    @Query(value = "SELECT m.id AS id, m.fecha_hora AS fecha_hora, i.nombre AS insumo_nombre, m.tipo AS tipo, "
            + "m.cantidad AS cantidad, i.unidad_medida AS unidad_medida, u.nombre AS usuario_nombre, "
            + "m.motivo_origen AS motivo_origen, m.referencia_tipo AS referencia_tipo, m.referencia_id AS referencia_id "
            + "FROM movimiento_inventario m "
            + "LEFT JOIN insumo i ON i.id = m.insumo_id "
            + "LEFT JOIN usuario u ON u.id = m.usuario_id "
            + "WHERE (:fechaInicio IS NULL OR m.fecha_hora::date >= :fechaInicio) "
            + "AND (:fechaFin IS NULL OR m.fecha_hora::date <= :fechaFin) "
            + "AND (:tipo IS NULL OR m.tipo = :tipo) "
            + "AND (:insumoId IS NULL OR m.insumo_id = :insumoId) "
            + "AND (:usuarioId IS NULL OR m.usuario_id = :usuarioId) "
            + "ORDER BY m.fecha_hora DESC", nativeQuery = true)
    List<MovimientoInventarioResumenRow> listar(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin,
                                                 @Param("tipo") String tipo,
                                                 @Param("insumoId") Integer insumoId,
                                                 @Param("usuarioId") Integer usuarioId);
}
