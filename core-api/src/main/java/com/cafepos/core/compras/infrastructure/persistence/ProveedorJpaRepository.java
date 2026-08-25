package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.Proveedor;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Nativa con CAST explicito a proposito (ver CajaJornadaJpaRepository/
 * VentaJpaRepository/FacturaDianJpaRepository, mismo criterio de
 * "':param IS NULL' en JPQL/nativo sin CAST puede fallar con
 * 'could not determine data type of parameter' — ver CLAUDE.md).
 */
interface ProveedorJpaRepository extends TenantAwareRepository<Proveedor, Integer> {

    @Query(value = "SELECT id AS id, codigo AS codigo, nombre AS nombre, nit AS nit, telefono AS telefono, "
            + "estado AS estado FROM proveedor "
            + "WHERE (CAST(:estado AS varchar) IS NULL OR estado = CAST(:estado AS varchar)) "
            + "AND (CAST(:q AS varchar) IS NULL OR nombre ILIKE '%' || CAST(:q AS varchar) || '%') "
            + "ORDER BY nombre", nativeQuery = true)
    List<ProveedorResumenRow> listar(@Param("estado") String estado, @Param("q") String q);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM compra WHERE proveedor_id = :proveedorId)", nativeQuery = true)
    boolean tieneComprasAsociadas(@Param("proveedorId") Integer proveedorId);
}
