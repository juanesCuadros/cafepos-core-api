package com.cafepos.core.operacion.domain;

/** estado_preparacion solo avanza hacia adelante (pendiente -> en_preparacion -> listo) — ver PedidoItem.transicionarEstado. */
public class TransicionEstadoInvalidaException extends RuntimeException {

    public TransicionEstadoInvalidaException(String estadoActual, String estadoNuevo) {
        super("No se puede regresar un ítem de '" + estadoActual + "' a '" + estadoNuevo + "'");
    }
}
