package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.Transaccion;

import java.util.List;

public record TransaccionesResponse(List<TransaccionResponse> transacciones) {

    public static TransaccionesResponse de(List<Transaccion> transacciones) {
        return new TransaccionesResponse(transacciones.stream().map(TransaccionResponse::de).toList());
    }
}
