package com.cafepos.core.gastos.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de CategoriaGasto — implementado en infrastructure.persistence. */
public interface CategoriaGastoRepository {

    CategoriaGasto guardar(CategoriaGasto categoriaGasto);

    Optional<CategoriaGasto> buscarPorId(Integer id);

    List<CategoriaGasto> listar();
}
