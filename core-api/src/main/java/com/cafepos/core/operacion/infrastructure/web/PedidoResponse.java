package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PedidoDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Respuesta de POST /pedidos — items siempre vacio y subtotal 0 (pedido recien creado, sin promociones_sugeridas). */
public record PedidoResponse(Integer id, String numeroOrden, MesaRefResponse mesa, UsuarioRefResponse usuario,
                              String estado, List<PedidoItemResponse> items, @Monto BigDecimal subtotal,
                              OffsetDateTime fechaApertura) {

    public static PedidoResponse de(PedidoDetalle detalle) {
        return new PedidoResponse(detalle.id(), detalle.numeroOrden(), MesaRefResponse.de(detalle.mesa()),
                UsuarioRefResponse.de(detalle.usuario()), detalle.estado(), List.of(), BigDecimal.ZERO,
                detalle.fechaApertura());
    }
}
