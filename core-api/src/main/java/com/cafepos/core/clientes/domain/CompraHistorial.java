package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de GET /clientes/{id}/historial — join venta -> factura_dian
 * (LEFT JOIN, facturaNumero puede venir null). venta/factura_dian
 * pertenecen al futuro modulo Caja, que todavia no existe — se lee la
 * tabla directo por nombre desde este modulo (ver ClienteJpaRepository),
 * mismo patron ya usado para configuracion_sistema/lote_insumo en
 * restaurante/inventario. Lista vacia hasta que Caja exista, eso es
 * correcto, no un bug.
 */
public record CompraHistorial(Integer ventaId, OffsetDateTime fechaHora, BigDecimal total, String facturaNumero,
                               String estado) {
}
