package com.cafepos.core.operacion.domain;

/** usuario.empleado_id es NULL — turno.empleado_id es NOT NULL, sin esto el INSERT fallaria crudo. */
public class UsuarioSinEmpleadoException extends RuntimeException {

    public UsuarioSinEmpleadoException() {
        super("Tu usuario no tiene un empleado asociado, contacta a tu Jefe");
    }
}
