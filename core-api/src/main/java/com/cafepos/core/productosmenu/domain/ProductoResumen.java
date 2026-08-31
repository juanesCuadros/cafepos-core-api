package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/**
 * Fila de GET /productos — categoria viene aplanada (categoriaId +
 * categoriaNombre), el join ya se hizo en SQL. visibilidad se agrega acá
 * (no está en el ejemplo abreviado de api_04_productos_menu.md 4.1) porque
 * sin ella, Operación → Pedidos (pedido-menu-panel.tsx) y la vista previa
 * del Menú Digital (MenuDigitalPage.tsx) no tienen forma de filtrar qué
 * productos son pedibles/visibles — el filtro cliente contra un campo
 * ausente siempre daba `undefined === "visible"` = false, así que NINGÚN
 * producto podía aparecer nunca en ninguna de esas dos pantallas.
 * manejaReceta se agrega por el mismo motivo: Venta Rápida decide si debe
 * habilitar "Enviar comanda" por ítem según este campo, en tiempo real
 * mientras se arma el pedido — sin él en el listado, ningún ítem podía
 * mandarse nunca a cocina (ver INTEGRACION.md hallazgo 3.37).
 */
public record ProductoResumen(Integer id, String codigo, String nombre, Integer categoriaId,
                               String categoriaNombre, String imagen, BigDecimal precioVenta, String estado,
                               String visibilidad, boolean manejaReceta) {
}
