package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PedidoDetalle;
import com.cafepos.core.productosmenu.domain.PromocionSugerida;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PedidoDetalleResponse(Integer id, String numeroOrden, MesaRefResponse mesa, UsuarioRefResponse usuario,
                                     String estado, List<PedidoItemResponse> items, @Monto BigDecimal subtotal,
                                     List<PromocionSugerida> promocionesSugeridas, OffsetDateTime fechaApertura) {

    public static PedidoDetalleResponse de(PedidoDetalle detalle) {
        return new PedidoDetalleResponse(detalle.id(), detalle.numeroOrden(), MesaRefResponse.de(detalle.mesa()),
                UsuarioRefResponse.de(detalle.usuario()), detalle.estado(),
                detalle.items().stream().map(PedidoItemResponse::de).toList(), detalle.subtotal(),
                detalle.promocionesSugeridas(), detalle.fechaApertura());
    }
}
