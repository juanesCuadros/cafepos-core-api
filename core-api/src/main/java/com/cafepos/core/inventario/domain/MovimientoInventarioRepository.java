package com.cafepos.core.inventario.domain;

import java.time.LocalDate;
import java.util.List;

/** Puerto de persistencia de MovimientoInventario — implementado en infrastructure.persistence. */
public interface MovimientoInventarioRepository {

    MovimientoInventario guardar(MovimientoInventario movimiento);

    List<MovimientoInventarioResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, String tipo,
                                              Integer insumoId, Integer usuarioId);
}
