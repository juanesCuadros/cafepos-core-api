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
            + "p.imagen AS imagen, p.precio_venta AS precio_venta, p.estado AS estado, "
            + "p.visibilidad AS visibilidad, p.maneja_receta AS maneja_receta "
            + "FROM producto p JOIN categoria c ON c.id = p.categoria_id "
            + "WHERE (CAST(:categoriaId AS int) IS NULL OR p.categoria_id = CAST(:categoriaId AS int)) "
            + "AND (CAST(:estado AS varchar) IS NULL OR p.estado = CAST(:estado AS varchar)) "
            + "AND (CAST(:q AS varchar) IS NULL OR p.nombre ILIKE '%' || CAST(:q AS varchar) || '%') "
            + "ORDER BY p.nombre", nativeQuery = true)
    List<ProductoResumenRow> listar(@Param("categoriaId") Integer categoriaId,
                                     @Param("estado") String estado,
                                     @Param("q") String q);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM area_cocina WHERE id = :id)", nativeQuery = true)
    boolean existeAreaCocina(@Param("id") Integer id);

    /**
     * Usada por el menu publico (com.cafepos.core.restaurante) — filtra mas
     * estricto que "listar" de arriba a proposito (categoria activa Y
     * producto activo Y visible), no es apta para la vista administrativa.
     */
    @Query(value = "SELECT c.nombre AS categoria_nombre, c.orden AS categoria_orden, p.nombre AS nombre, "
            + "p.descripcion AS descripcion, p.precio_venta AS precio_venta, p.imagen AS imagen "
            + "FROM producto p JOIN categoria c ON c.id = p.categoria_id "
            + "WHERE c.estado = 'activa' AND p.estado = 'activo' AND p.visibilidad = 'visible' "
            + "ORDER BY c.orden, c.nombre, p.nombre", nativeQuery = true)
    List<ProductoPublicoRow> listarVisiblesParaMenuPublico();

    @Query(value = "SELECT EXISTS(SELECT 1 FROM pedido_item pi "
            + "JOIN venta v ON v.pedido_id = pi.pedido_id "
            + "WHERE pi.producto_id = :productoId)", nativeQuery = true)
    boolean tieneVentasAsociadas(@Param("productoId") Integer productoId);
}
