package com.cafepos.core.caja.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de CajaJornada — implementado en infrastructure.persistence. */
public interface CajaJornadaRepository {

    /** Lanza JornadaYaAbiertaException si viola el indice unico parcial (RN-011) — ver adapter. */
    CajaJornada guardar(CajaJornada jornada);

    Optional<CajaJornada> buscarAbierta();

    Optional<CajaJornada> buscarPorId(Integer id);

    /** Jornadas (abiertas o cerradas) con fecha_apertura dentro de [fechaInicio, fechaFin] — ver CajaHistorialService. */
    List<CajaJornada> listarEnRango(LocalDate fechaInicio, LocalDate fechaFin);
}
