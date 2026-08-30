package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.application.EmpleadoDetalleVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

/** GET /empleados/{id} — cedula SIN enmascarar, unico lugar donde aparece completa (mismo criterio que Cliente). */
public record EmpleadoDetalleResponse(Integer id, String codigo, String nombre, String cedula, String cargo,
                                       String telefono, String estado, UsuarioAsociadoResponse usuarioAsociado,
                                       ResumenTurnosMesResponse resumenTurnosMesActual,
                                       @Monto BigDecimal resumenPropinasMesActual) {

    public static EmpleadoDetalleResponse de(EmpleadoDetalleVista vista) {
        var e = vista.empleado();
        return new EmpleadoDetalleResponse(e.getId(), e.getCodigo(), e.getNombre(), e.getCedula(), e.getCargo(),
                e.getTelefono(), e.getEstado(), UsuarioAsociadoResponse.de(vista.usuarioAsociado()),
                ResumenTurnosMesResponse.de(vista.resumenTurnosMesActual()), vista.resumenPropinasMesActual());
    }
}
