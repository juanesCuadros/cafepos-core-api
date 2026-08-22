package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.PromocionProducto;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PromocionProductoJpaRepository extends TenantAwareRepository<PromocionProducto, Integer> {

    List<PromocionProducto> findByPromocionId(Integer promocionId);

    @Query(value = "SELECT p.id AS id, p.nombre AS nombre FROM promocion_producto pp "
            + "JOIN producto p ON p.id = pp.producto_id "
            + "WHERE pp.promocion_id = :promocionId ORDER BY p.nombre", nativeQuery = true)
    List<ProductoRefRow> productosDe(@Param("promocionId") Integer promocionId);
}
