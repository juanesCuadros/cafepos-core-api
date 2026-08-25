package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsPedidoVista;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * enviadoAt incluido a proposito — el frontend (kds.types.ts) ya lo
 * esperaba como "enviado_at", se habia quedado afuera. Nombre del campo
 * elegido para que coincida con esa clave JSON (SNAKE_CASE global) — el
 * dominio internamente lo llama fechaEnviado, no hace falta que coincidan.
 */
public record KdsPedidoResponse(Integer pedidoId, String numeroOrden, String mesa, String tipo,
                                 OffsetDateTime enviadoAt, List<KdsItemResponse> items) {

    public static KdsPedidoResponse de(KdsPedidoVista vista) {
        return new KdsPedidoResponse(vista.pedidoId(), vista.numeroOrden(), vista.mesa(), vista.tipo(),
                vista.fechaEnviado(), vista.items().stream().map(KdsItemResponse::de).toList());
    }
}
