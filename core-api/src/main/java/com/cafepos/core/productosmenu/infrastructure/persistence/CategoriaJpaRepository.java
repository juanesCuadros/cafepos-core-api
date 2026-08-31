package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Categoria;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CategoriaJpaRepository extends TenantAwareRepository<Categoria, Integer> {

    /**
     * Proyeccion nativa con COUNT — nunca cargar todos los productos a
     * memoria para contarlos en Java. RLS filtra tanto categoria como
     * producto por tenant_id automaticamente (ver TenantAwareRepository).
     */
    @Query(value = "SELECT c.id AS id, c.icono AS icono, c.nombre AS nombre, c.descripcion AS descripcion, "
            + "c.orden AS orden, c.estado AS estado, COUNT(p.id) AS num_productos "
            + "FROM categoria c LEFT JOIN producto p ON p.categoria_id = c.id "
            + "GROUP BY c.id, c.icono, c.nombre, c.descripcion, c.orden, c.estado "
            + "ORDER BY c.orden", nativeQuery = true)
    List<CategoriaResumenRow> listarConNumProductos();

    @Query(value = "SELECT COUNT(*) FROM producto WHERE categoria_id = :categoriaId", nativeQuery = true)
    long contarProductos(@Param("categoriaId") Integer categoriaId);
}
