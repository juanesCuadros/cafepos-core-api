package com.cafepos.core.operacion.infrastructure.web;

public record MarcarListaCobrarResponse(String mensaje, Integer mesaId, String estado) {

    private static final String ESTADO_LISTA_COBRAR = "lista_cobrar";

    public static MarcarListaCobrarResponse de(Integer mesaId) {
        return new MarcarListaCobrarResponse("Mesa marcada como lista para cobrar", mesaId, ESTADO_LISTA_COBRAR);
    }
}
