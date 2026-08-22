package com.cafepos.core.productosmenu.domain;

/** El grupo no existe, o no pertenece a ESTE combo especificamente (un grupo_id valido de OTRO combo cae aca tambien). */
public class ComboGrupoNoEncontradoException extends RuntimeException {

    public ComboGrupoNoEncontradoException() {
        super("Grupo no encontrado");
    }
}
