package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

/**
 * id incluido a proposito: el frontend lo necesita como pedido_item_id al
 * armar POST /devoluciones (ver api_03_caja.md 3.7) — sin el, la pantalla de
 * Devoluciones no tiene de donde sacar que item devolver. estadoPreparacion
 * tambien incluido — RN-023/024: el frontend lo muestra como referencia de
 * que metodo_reembolso va a elegir el backend al confirmar la devolucion.
 */
public record VentaItemDetalleResponse(Integer id, String nombre, BigDecimal cantidad,
                                        @Monto BigDecimal precioUnitario, @Monto BigDecimal subtotal,
                                        String estadoPreparacion) {

    public static VentaItemDetalleResponse de(PedidoItemParaVenta item) {
        return new VentaItemDetalleResponse(item.id(), item.nombre(), item.cantidad(), item.precioUnitario(),
                item.precioUnitario().multiply(item.cantidad()), item.estadoPreparacion());
    }
}
