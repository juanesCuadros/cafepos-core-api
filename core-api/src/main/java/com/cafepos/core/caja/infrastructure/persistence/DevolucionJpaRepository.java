package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.Devolucion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

interface DevolucionJpaRepository extends TenantAwareRepository<Devolucion, Integer> {

    /** Nativa con CAST explicito a proposito (mismo problema de ":param IS NULL" en JPQL, ver CLAUDE.md). */
    @Query(value = "SELECT d.id AS id, v.codigo AS venta_codigo, d.fecha AS fecha, c.nombre AS cliente_nombre, "
            + "d.monto_devuelto AS monto_devuelto, d.metodo_reembolso AS metodo_reembolso, d.estado AS estado "
            + "FROM devolucion d JOIN venta v ON v.id = d.venta_id LEFT JOIN cliente c ON c.id = v.cliente_id "
            + "WHERE (CAST(:fechaInicio AS timestamptz) IS NULL OR d.fecha >= CAST(:fechaInicio AS timestamptz)) "
            + "AND (CAST(:fechaFin AS timestamptz) IS NULL OR d.fecha < CAST(:fechaFin AS timestamptz)) "
            + "AND (CAST(:estado AS varchar) IS NULL OR d.estado = CAST(:estado AS varchar)) "
            + "ORDER BY d.fecha DESC", nativeQuery = true)
    List<DevolucionListadoRow> listar(@Param("fechaInicio") OffsetDateTime fechaInicio,
                                       @Param("fechaFin") OffsetDateTime fechaFin, @Param("estado") String estado);
}
