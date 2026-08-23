package com.cafepos.core.inventario.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Conteo/ConteoDetalle — implementado en infrastructure.persistence. */
public interface ConteoRepository {

    Conteo guardar(Conteo conteo);

    Optional<Conteo> buscarPorId(Integer id);

    void guardarDetalle(List<ConteoDetalle> detalles);

    List<ConteoResumen> listar();

    /** Todas las filas del detalle, tengan o no diferencia — usado por el POST y por GET /conteos/{id}. */
    List<ConteoDetalleItem> detalleDe(Integer conteoId);
}
