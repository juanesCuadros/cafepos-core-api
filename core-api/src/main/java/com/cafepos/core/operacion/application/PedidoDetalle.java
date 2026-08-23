package com.cafepos.core.operacion.application;

import com.cafepos.core.productosmenu.domain.PromocionSugerida;
import com.cafepos.core.restaurante.domain.MesaResumen;
import com.cafepos.core.shared.seguridad.Usuario;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Vista completa de un Pedido — ver PedidoService.abrir/obtenerDetalle. */
public record PedidoDetalle(Integer id, String numeroOrden, MesaResumen mesa, Usuario usuario, String estado,
                             List<PedidoItemDetalle> items, BigDecimal subtotal,
                             List<PromocionSugerida> promocionesSugeridas, OffsetDateTime fechaApertura) {
}
