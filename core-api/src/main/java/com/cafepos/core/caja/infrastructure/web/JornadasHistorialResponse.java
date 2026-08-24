package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaArqueoVista;

import java.util.List;

public record JornadasHistorialResponse(List<JornadaHistorialItemResponse> jornadas) {

    public static JornadasHistorialResponse de(List<JornadaArqueoVista> vistas) {
        return new JornadasHistorialResponse(vistas.stream().map(JornadaHistorialItemResponse::de).toList());
    }
}
