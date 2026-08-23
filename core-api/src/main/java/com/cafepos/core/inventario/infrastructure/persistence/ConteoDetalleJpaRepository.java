package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.ConteoDetalle;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ConteoDetalleJpaRepository extends TenantAwareRepository<ConteoDetalle, Integer> {

    @Query(value = "SELECT i.nombre AS insumo_nombre, cd.stock_sistema AS stock_sistema, "
            + "cd.stock_fisico AS stock_fisico, cd.diferencia AS diferencia "
            + "FROM conteo_detalle cd JOIN insumo i ON i.id = cd.insumo_id "
            + "WHERE cd.conteo_id = :conteoId ORDER BY i.nombre", nativeQuery = true)
    List<ConteoDetalleItemRow> detalleDe(@Param("conteoId") Integer conteoId);
}
