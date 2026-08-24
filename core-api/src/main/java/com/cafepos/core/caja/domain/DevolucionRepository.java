package com.cafepos.core.caja.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Devolucion — implementado en infrastructure.persistence. */
public interface DevolucionRepository {

    Devolucion guardar(Devolucion devolucion);

    Optional<Devolucion> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo) — ver DevolucionService. */
    List<DevolucionListadoItem> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String estado);
}
