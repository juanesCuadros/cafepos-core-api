package com.cafepos.core.shared.seguridad;

/** El (modulo, accion) pedido no existe en el catalogo global "permiso" (ver V2__catalogo_permisos.sql). */
public class PermisoNoEncontradoException extends RuntimeException {

    public PermisoNoEncontradoException() {
        super("Permiso no encontrado para el módulo y acción indicados");
    }
}
