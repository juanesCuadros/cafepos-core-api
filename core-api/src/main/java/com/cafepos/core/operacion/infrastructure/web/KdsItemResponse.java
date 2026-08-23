package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsItemVista;

import java.math.BigDecimal;

public record KdsItemResponse(Integer id, String nombre, BigDecimal cantidad, String observacion,
                               String estadoPreparacion) {

    public static KdsItemResponse de(KdsItemVista item) {
        return new KdsItemResponse(item.id(), item.nombre(), item.cantidad(), item.observacion(),
                item.estadoPreparacion());
    }
}
