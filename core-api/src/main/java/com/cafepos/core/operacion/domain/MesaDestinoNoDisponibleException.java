package com.cafepos.core.operacion.domain;

/** La mesa destino de un mover-mesa no esta en estado libre — ver PedidoService.moverMesa. */
public class MesaDestinoNoDisponibleException extends RuntimeException {

    public MesaDestinoNoDisponibleException() {
        super("La mesa destino no está disponible");
    }
}
