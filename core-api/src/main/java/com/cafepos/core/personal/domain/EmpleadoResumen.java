package com.cafepos.core.personal.domain;

/** Fila de GET /empleados — cedula YA enmascarada, nunca la real (ver Empleado.getCedulaEnmascarada). */
public record EmpleadoResumen(Integer id, String codigo, String nombre, String cedulaEnmascarada, String cargo,
                               String telefono, String estado) {
}
