package com.cafepos.core.caja.domain;

import java.util.List;

/** Puerto de persistencia de CajaMovimiento — implementado en infrastructure.persistence. */
public interface CajaMovimientoRepository {

    CajaMovimiento guardar(CajaMovimiento movimiento);

    List<CajaMovimiento> listarDeJornada(Integer jornadaId);
}
