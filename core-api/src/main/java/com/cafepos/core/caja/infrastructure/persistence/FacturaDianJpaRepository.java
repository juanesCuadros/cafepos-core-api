package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

interface FacturaDianJpaRepository extends TenantAwareRepository<FacturaDian, Integer> {

    Optional<FacturaDian> findByVentaId(Integer ventaId);

    /**
     * Nativa con CAST explicito a proposito (ver CajaJornadaJpaRepository/
     * VentaJpaRepository, mismo problema de ":param IS NULL" en JPQL). cliente
     * y numeroFactura filtran con ILIKE parcial.
     */
    @Query(value = "SELECT f.id AS id, f.numero_factura AS numero_factura, f.fecha_emision AS fecha_emision, "
            + "c.nombre AS cliente_nombre, v.total AS total, f.estado_dian AS estado_dian "
            + "FROM factura_dian f JOIN venta v ON v.id = f.venta_id LEFT JOIN cliente c ON c.id = v.cliente_id "
            + "WHERE (CAST(:fechaInicio AS timestamptz) IS NULL OR f.fecha_emision >= CAST(:fechaInicio AS timestamptz)) "
            + "AND (CAST(:fechaFin AS timestamptz) IS NULL OR f.fecha_emision < CAST(:fechaFin AS timestamptz)) "
            + "AND (CAST(:estadoDian AS varchar) IS NULL OR f.estado_dian = CAST(:estadoDian AS varchar)) "
            + "AND (CAST(:cliente AS varchar) IS NULL OR c.nombre ILIKE '%' || CAST(:cliente AS varchar) || '%') "
            + "AND (CAST(:numeroFactura AS varchar) IS NULL OR f.numero_factura ILIKE '%' || CAST(:numeroFactura AS varchar) || '%') "
            + "ORDER BY f.fecha_emision DESC", nativeQuery = true)
    List<FacturaListadoRow> listar(@Param("fechaInicio") OffsetDateTime fechaInicio,
                                    @Param("fechaFin") OffsetDateTime fechaFin,
                                    @Param("estadoDian") String estadoDian, @Param("cliente") String cliente,
                                    @Param("numeroFactura") String numeroFactura);
}
