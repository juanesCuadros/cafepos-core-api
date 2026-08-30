package com.cafepos.core.personal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Fila de GET /turnos — empleado_nombre aplanado, el join ya se hizo en SQL.
 * empleadoId y observaciones se agregan (no estan en el ejemplo abreviado de
 * api_08_personal.md 8.2) porque sin ellos el frontend no puede preseleccionar
 * el empleado correcto ni mostrar la observacion real al editar un turno
 * existente (ver INTEGRACION.md hallazgo 3.22) — ambas columnas ya existen en
 * la tabla turno, esto solo las expone en el listado.
 */
public record TurnoResumen(Integer id, Integer empleadoId, String empleadoNombre, LocalDate fecha,
                            OffsetDateTime horaInicio, OffsetDateTime horaFin, BigDecimal horasTrabajadas,
                            String observaciones) {
}
