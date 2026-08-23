package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Perdida;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

interface PerdidaJpaRepository extends TenantAwareRepository<Perdida, Integer> {

    @Query(value = "SELECT p.id AS id, p.fecha AS fecha, i.nombre AS insumo_nombre, p.cantidad AS cantidad, "
            + "p.motivo AS motivo, p.costo_calculado AS costo_calculado, u.nombre AS usuario_nombre "
            + "FROM perdida p "
            + "JOIN insumo i ON i.id = p.insumo_id "
            + "JOIN usuario u ON u.id = p.usuario_id "
            + "WHERE (:fechaInicio IS NULL OR p.fecha >= :fechaInicio) "
            + "AND (:fechaFin IS NULL OR p.fecha <= :fechaFin) "
            + "AND (:categoriaInsumoId IS NULL OR i.categoria_insumo_id = :categoriaInsumoId) "
            + "AND (:motivo IS NULL OR p.motivo = :motivo) "
            + "ORDER BY p.fecha DESC", nativeQuery = true)
    List<PerdidaResumenRow> listar(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                                    @Param("categoriaInsumoId") Integer categoriaInsumoId,
                                    @Param("motivo") String motivo);
}
