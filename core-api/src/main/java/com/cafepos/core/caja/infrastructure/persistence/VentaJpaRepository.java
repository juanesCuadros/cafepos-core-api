package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

interface VentaJpaRepository extends TenantAwareRepository<Venta, Integer> {

    /**
     * Cada filtro es opcional (:param IS NULL lo desactiva) — nativa con
     * CAST explicito a proposito: JPQL con ":param IS NULL OR ..." genera
     * un ? por cada ocurrencia del parametro, y Postgres no puede inferir
     * el tipo de un ? usado solo en "? IS NULL" sin contexto — error real
     * en runtime ("could not determine data type of parameter $1"), no se
     * ve en compilacion (ver CajaJornadaJpaRepository.listarEnRango, mismo
     * problema). El filtro por metodoPagoId usa EXISTS contra venta_pago.
     */
    @Query(value = "SELECT * FROM venta WHERE "
            + "(CAST(:fechaInicio AS timestamptz) IS NULL OR fecha_hora >= CAST(:fechaInicio AS timestamptz)) AND "
            + "(CAST(:fechaFin AS timestamptz) IS NULL OR fecha_hora < CAST(:fechaFin AS timestamptz)) AND "
            + "(CAST(:estado AS varchar) IS NULL OR estado = CAST(:estado AS varchar)) AND "
            + "(CAST(:cajeroId AS int) IS NULL OR cajero_id = CAST(:cajeroId AS int)) AND "
            + "(CAST(:metodoPagoId AS int) IS NULL OR EXISTS (SELECT 1 FROM venta_pago vp WHERE vp.venta_id = venta.id "
            + "AND vp.metodo_pago_id = CAST(:metodoPagoId AS int))) "
            + "ORDER BY fecha_hora DESC", nativeQuery = true)
    List<Venta> listar(@Param("fechaInicio") OffsetDateTime fechaInicio, @Param("fechaFin") OffsetDateTime fechaFin,
                        @Param("metodoPagoId") Integer metodoPagoId, @Param("estado") String estado,
                        @Param("cajeroId") Integer cajeroId);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.jornadaId = :jornadaId AND v.estado = 'cobrado'")
    BigDecimal sumaTotalCobradoDeJornada(@Param("jornadaId") Integer jornadaId);
}
