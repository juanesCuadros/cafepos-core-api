package com.cafepos.core.restaurante.domain;

/**
 * GET /menu-publico rechazado — mismo mensaje sin importar si el menu esta
 * desactivado o si el restaurante no esta configurado, a proposito (no
 * revela cual de las dos paso, ver MenuPublicoService.obtener).
 */
public class MenuPublicoNoDisponibleException extends RuntimeException {

    public MenuPublicoNoDisponibleException() {
        super("Menú no disponible");
    }
}
