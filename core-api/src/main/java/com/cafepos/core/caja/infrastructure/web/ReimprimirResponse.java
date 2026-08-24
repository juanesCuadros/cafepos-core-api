package com.cafepos.core.caja.infrastructure.web;

public record ReimprimirResponse(String mensaje) {

    public static final ReimprimirResponse ENVIADO = new ReimprimirResponse("Enviado a impresora");
}
