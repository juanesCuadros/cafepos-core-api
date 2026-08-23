package com.cafepos.core.operacion.domain;

/** El request de un combo no trajo seleccion para todos sus grupos. */
public class ComboSeleccionIncompletaException extends RuntimeException {

    public ComboSeleccionIncompletaException(String nombreGrupo) {
        super("Debes elegir una opción para el grupo '" + nombreGrupo + "'");
    }
}
