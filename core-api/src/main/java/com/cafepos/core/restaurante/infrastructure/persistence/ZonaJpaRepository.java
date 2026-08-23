package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Zona;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ZonaJpaRepository extends TenantAwareRepository<Zona, Integer> {

    /** RLS filtra tanto zona como mesa por tenant_id automaticamente (ver TenantAwareRepository). */
    @Query(value = "SELECT z.id AS id, z.codigo AS codigo, z.icono AS icono, z.nombre AS nombre, "
            + "z.estado AS estado, COUNT(m.id) AS num_mesas "
            + "FROM zona z LEFT JOIN mesa m ON m.zona_id = z.id "
            + "GROUP BY z.id, z.codigo, z.icono, z.nombre, z.estado "
            + "ORDER BY z.nombre", nativeQuery = true)
    List<ZonaResumenRow> listarConNumMesas();

    @Query(value = "SELECT COUNT(*) FROM mesa WHERE zona_id = :zonaId", nativeQuery = true)
    long contarMesas(@Param("zonaId") Integer zonaId);
}
