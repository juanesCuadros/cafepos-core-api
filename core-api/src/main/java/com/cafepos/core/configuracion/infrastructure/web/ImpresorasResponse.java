package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.Impresora;

import java.util.List;

public record ImpresorasResponse(List<ImpresoraResponse> impresoras) {

    public static ImpresorasResponse de(List<Impresora> impresoras) {
        return new ImpresorasResponse(impresoras.stream().map(ImpresoraResponse::de).toList());
    }
}
