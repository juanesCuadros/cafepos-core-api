package com.cafepos.core.operacion.domain;

/** El combo_id no existe (ver productosmenu.ComboService.buscarParaPedido via NamedInterface). */
public class ComboNoEncontradoException extends RuntimeException {

    public ComboNoEncontradoException() {
        super("Combo no encontrado");
    }
}
