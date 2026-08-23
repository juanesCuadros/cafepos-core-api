package com.cafepos.core.clientes.domain;

/** DELETE /clientes/{id} rechazado — tiene ventas asociadas o saldo_favor > 0, ver ClienteService.eliminar. */
public class ClienteNoEliminableException extends RuntimeException {

    public ClienteNoEliminableException() {
        super("No se puede eliminar, este cliente tiene historial de compras o saldo a favor pendiente");
    }
}
