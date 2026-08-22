package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Producto;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ProductoJpaRepository extends TenantAwareRepository<Producto, Integer> {

    /**
     * Filtros opcionales (categoria_id, estado, q) resueltos con OR ... IS NULL
     * en vez de armar la query dinamicamente en Java - RLS filtra por
     * tenant_id automaticamente (ver TenantAwareRepository), igual que en
     * CategoriaJpaRepository. "categoria" viaja anidado en la respuesta del
     * contrato, por eso el JOIN aca en vez de cargar la entidad completa de
     * Categoria solo para nombre+id.
     */
    @Query(value = "SELECT p.id AS id, p.codigo AS codigo, p.nombre AS nombre, "
            + "c.id AS categoria_id, c.nombre AS categoria_nombre, "
            + "p.imagen AS imagen, p.precio_venta AS precio_venta, p.estado AS estado "
            + "FROM producto p JOIN categoria c ON c.id = p.categoria_id "
            + "WHERE (:categoriaId IS NULL OR p.categoria_id = :categoriaId) "
            + "AND (:estado IS NULL OR p.estado = :estado) "
            + "AND (:q IS NULL OR p.nombre ILIKE '%' || :q || '%') "
            + "ORDER BY p.nombre", nativeQuery = true)
    List<ProductoResumenRow> listar(@Param("categoriaId") Integer categoriaId,
                                     @Param("estado") String estado,
                                     @Param("q") String q);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM area_cocina WHERE id = :id)", nativeQuery = true)
    boolean existeAreaCocina(@Param("id") Integer id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM pedido_item pi "
            + "JOIN venta v ON v.pedido_id = pi.pedido_id "
            + "WHERE pi.producto_id = :productoId)", nativeQuery = true)
    boolean tieneVentasAsociadas(@Param("productoId") Integer productoId);
}
