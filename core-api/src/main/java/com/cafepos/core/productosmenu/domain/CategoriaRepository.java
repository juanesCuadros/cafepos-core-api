package com.cafepos.core.productosmenu.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Categoria — implementado en infrastructure.persistence. */
public interface CategoriaRepository {

    Categoria guardar(Categoria categoria);

    Optional<Categoria> buscarPorId(Integer id);

    List<CategoriaResumen> listarConNumProductos();

    long contarProductos(Integer categoriaId);

    void eliminar(Categoria categoria);
}
