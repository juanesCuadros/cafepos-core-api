package com.cafepos.core.gastos.infrastructure.persistence;

import com.cafepos.core.gastos.domain.Gasto;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/** Nativa con CAST explicito en cada ocurrencia de cada parametro opcional (ver CLAUDE.md). fecha es DATE, comparacion directa. */
interface GastoJpaRepository extends TenantAwareRepository<Gasto, Integer> {

    @Query(value = "SELECT g.id AS id, g.codigo AS codigo, g.fecha AS fecha, c.nombre AS categoria, "
            + "g.descripcion AS descripcion, g.monto AS monto, g.metodo_pago AS metodo_pago, u.nombre AS usuario "
            + "FROM gasto g JOIN categoria_gasto c ON c.id = g.categoria_gasto_id JOIN usuario u ON u.id = g.usuario_id "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR g.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR g.fecha <= CAST(:fechaFin AS date)) "
            + "AND (CAST(:categoriaGastoId AS int) IS NULL OR g.categoria_gasto_id = CAST(:categoriaGastoId AS int)) "
            + "AND (CAST(:metodoPago AS varchar) IS NULL OR g.metodo_pago = CAST(:metodoPago AS varchar)) "
            + "ORDER BY g.fecha DESC, g.id DESC", nativeQuery = true)
    List<GastoResumenRow> listar(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                                  @Param("categoriaGastoId") Integer categoriaGastoId,
                                  @Param("metodoPago") String metodoPago);
}
