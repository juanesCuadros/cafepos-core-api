package com.cafepos.core.productosmenu.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Producto — implementado en infrastructure.persistence. */
public interface ProductoRepository {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Integer id);

    List<ProductoResumen> listar(Integer categoriaId, String estado, String q);

    boolean existeAreaCocina(Integer areaCocinaId);

    /** Usado por restaurante.MenuPublicoService — ver ProductoPublico. */
    List<ProductoPublico> listarVisiblesParaMenuPublico();

    /** join pedido_item -> venta por pedido_id: solo cuenta si el pedido llego a facturarse, no cualquier pedido. */
    boolean tieneVentasAsociadas(Integer productoId);

    void eliminar(Producto producto);
}
