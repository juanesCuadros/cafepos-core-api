package com.cafepos.core.personal.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Turno (propio de este modulo) — implementado en infrastructure.persistence. */
public interface TurnoRepository {

    Turno guardar(Turno turno);

    Optional<Turno> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo). */
    List<TurnoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId);

    void eliminar(Turno turno);
}
