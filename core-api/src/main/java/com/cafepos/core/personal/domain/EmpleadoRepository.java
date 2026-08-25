package com.cafepos.core.personal.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Empleado (propio de este modulo) — implementado en infrastructure.persistence. */
public interface EmpleadoRepository {

    Empleado guardar(Empleado empleado);

    Optional<Empleado> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo). */
    List<EmpleadoResumen> listar(String cargo, String estado, String q);

    void eliminar(Empleado empleado);

    /** Optional.empty() si ningun usuario tiene este empleado_id — el indice unico (V27) garantiza a lo sumo una fila. */
    Optional<UsuarioAsociado> buscarUsuarioAsociado(Integer empleadoId);

    /** COUNT y SUM(horas_trabajadas) de turno para este empleado, mes calendario actual. */
    ResumenTurnosMes resumenTurnosMesActual(Integer empleadoId);
}
