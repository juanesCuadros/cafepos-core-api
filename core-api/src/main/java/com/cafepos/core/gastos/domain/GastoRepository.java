package com.cafepos.core.gastos.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Gasto — implementado en infrastructure.persistence. */
public interface GastoRepository {

    Gasto guardar(Gasto gasto);

    Optional<Gasto> buscarPorId(Integer id);

    List<GastoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaGastoId, String metodoPago);

    void eliminar(Gasto gasto);
}
