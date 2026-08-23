package com.cafepos.core.inventario.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de CategoriaInsumo — implementado en infrastructure.persistence. */
public interface CategoriaInsumoRepository {

    CategoriaInsumo guardar(CategoriaInsumo categoriaInsumo);

    Optional<CategoriaInsumo> buscarPorId(Integer id);

    List<CategoriaInsumo> listar();
}
