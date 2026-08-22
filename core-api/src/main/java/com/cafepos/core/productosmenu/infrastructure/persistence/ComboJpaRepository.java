package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface ComboJpaRepository extends TenantAwareRepository<Combo, Integer> {

    @Query(value = "SELECT id AS id, codigo AS codigo, nombre AS nombre, precio AS precio, estado AS estado "
            + "FROM combo ORDER BY nombre", nativeQuery = true)
    List<ComboResumenRow> listar();
}
