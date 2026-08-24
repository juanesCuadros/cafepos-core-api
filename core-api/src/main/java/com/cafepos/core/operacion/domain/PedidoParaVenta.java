package com.cafepos.core.operacion.domain;

import java.util.List;

/**
 * Datos minimos de un Pedido que com.cafepos.core.caja necesita para
 * cobrar (POST /ventas) — nunca la entidad Pedido completa.
 *
 * @NamedInterface propio, ver PedidoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("pedidoParaVenta")
public record PedidoParaVenta(Integer id, String tipo, String estado, Integer mesaId, Integer usuarioId,
                               List<PedidoItemParaVenta> items) {

    public boolean estaCerrado() {
        return Pedido.ESTADO_CERRADO.equals(estado);
    }

    public boolean esMesa() {
        return Pedido.TIPO_MESA.equals(tipo);
    }
}
