package com.cafepos.core.personal.application;

import com.cafepos.core.personal.domain.Empleado;
import com.cafepos.core.personal.domain.ResumenTurnosMes;
import com.cafepos.core.personal.domain.UsuarioAsociado;

import java.math.BigDecimal;

/** GET /empleados/{id} — usuarioAsociado null si ningun usuario tiene este empleado_id. */
public record EmpleadoDetalleVista(Empleado empleado, UsuarioAsociado usuarioAsociado,
                                    ResumenTurnosMes resumenTurnosMesActual, BigDecimal resumenPropinasMesActual) {
}
