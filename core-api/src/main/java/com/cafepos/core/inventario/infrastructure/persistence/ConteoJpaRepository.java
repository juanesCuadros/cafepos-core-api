package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Conteo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface ConteoJpaRepository extends TenantAwareRepository<Conteo, Integer> {

    @Query(value = "SELECT c.id AS id, c.fecha AS fecha, u.nombre AS usuario_nombre, "
            + "COUNT(cd.id) AS num_insumos, "
            + "COUNT(cd.id) FILTER (WHERE cd.diferencia <> 0) AS num_diferencias "
            + "FROM conteo c "
            + "JOIN usuario u ON u.id = c.usuario_id "
            + "LEFT JOIN conteo_detalle cd ON cd.conteo_id = c.id "
            + "GROUP BY c.id, c.fecha, u.nombre "
            + "ORDER BY c.fecha DESC", nativeQuery = true)
    List<ConteoResumenRow> listar();
}
