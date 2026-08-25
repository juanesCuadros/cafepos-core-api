package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.Compra;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/** Nativa con CAST explicito en cada ocurrencia de cada parametro opcional (ver CLAUDE.md). fecha es DATE, no TIMESTAMPTZ — comparacion directa, sin necesitar el +1 dia de un rango timestamptz. */
interface CompraJpaRepository extends TenantAwareRepository<Compra, Integer> {

    @Query(value = "SELECT c.id AS id, c.codigo AS codigo, c.fecha AS fecha, c.proveedor_id AS proveedor_id, "
            + "p.nombre AS proveedor_nombre, c.forma_pago AS forma_pago, c.estado AS estado, c.total AS total "
            + "FROM compra c JOIN proveedor p ON p.id = c.proveedor_id "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR c.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR c.fecha <= CAST(:fechaFin AS date)) "
            + "AND (CAST(:proveedorId AS int) IS NULL OR c.proveedor_id = CAST(:proveedorId AS int)) "
            + "AND (CAST(:formaPago AS varchar) IS NULL OR c.forma_pago = CAST(:formaPago AS varchar)) "
            + "AND (CAST(:estado AS varchar) IS NULL OR c.estado = CAST(:estado AS varchar)) "
            + "ORDER BY c.fecha DESC, c.id DESC", nativeQuery = true)
    List<CompraListadoRow> listar(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                                   @Param("proveedorId") Integer proveedorId, @Param("formaPago") String formaPago,
                                   @Param("estado") String estado);
}
