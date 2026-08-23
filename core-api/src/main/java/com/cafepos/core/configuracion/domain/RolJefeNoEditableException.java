package com.cafepos.core.configuracion.domain;

public class RolJefeNoEditableException extends RuntimeException {

    public RolJefeNoEditableException() {
        super("El rol Jefe no se puede modificar, siempre tiene acceso total");
    }
}
