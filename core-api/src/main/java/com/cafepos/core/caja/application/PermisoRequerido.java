package com.cafepos.core.caja.application;

/**
 * modulo+accion que el controller debe chequear con PermissionEvaluator
 * ANTES de ejecutar POST /ventas o POST /ventas/{id}/finalizar-entrega —
 * ver VentaService.determinarPermisoParaCobrar/ParaFinalizarEntrega. Se
 * resuelve dinamicamente segun pedido.tipo (mesa -> caja.pos, venta_rapida
 * -> caja.venta_rapida), no con @PreAuthorize estatico.
 */
public record PermisoRequerido(String modulo, String accion) {
}
