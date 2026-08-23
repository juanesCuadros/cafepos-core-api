package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Mesa;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MesaJpaRepository extends TenantAwareRepository<Mesa, Integer> {

    @Query(value = "SELECT id AS id, codigo AS codigo, numero AS numero, capacidad AS capacidad, "
            + "estado AS estado FROM mesa WHERE zona_id = :zonaId ORDER BY numero", nativeQuery = true)
    List<MesaResumenRow> listarDeZona(@Param("zonaId") Integer zonaId);
}
