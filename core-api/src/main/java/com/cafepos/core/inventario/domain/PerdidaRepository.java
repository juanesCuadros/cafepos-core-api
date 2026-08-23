package com.cafepos.core.inventario.domain;

import java.time.LocalDate;
import java.util.List;

/** Puerto de persistencia de Perdida — implementado en infrastructure.persistence. */
public interface PerdidaRepository {

    Perdida guardar(Perdida perdida);

    List<PerdidaResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaInsumoId, String motivo);
}
