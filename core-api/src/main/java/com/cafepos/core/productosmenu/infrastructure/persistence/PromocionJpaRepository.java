package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Promocion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface PromocionJpaRepository extends TenantAwareRepository<Promocion, Integer> {

    @Query(value = "SELECT id AS id, nombre AS nombre, tipo_descuento AS tipo_descuento, "
            + "vigencia_inicio AS vigencia_inicio, vigencia_fin AS vigencia_fin, estado AS estado "
            + "FROM promocion ORDER BY nombre", nativeQuery = true)
    List<PromocionResumenRow> listar();
}
