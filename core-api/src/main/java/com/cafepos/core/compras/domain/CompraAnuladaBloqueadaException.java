package com.cafepos.core.compras.domain;

/** 403 de negocio (distinto del 403 de PIN faltante/invalido) — solo si forma_pago='credito' Y estado='pagada'. */
public class CompraAnuladaBloqueadaException extends RuntimeException {

    public CompraAnuladaBloqueadaException() {
        super("No se puede anular una compra de credito que ya fue pagada");
    }
}
