package com.cafepos.core.productosmenu.domain;

/**
 * Fila de GET /categorias — numProductos es calculado (COUNT), no una columna
 * de categoria. descripcion se agrega acá (no está en el ejemplo abreviado
 * de api_04_productos_menu.md 4.2, que solo documenta id/icono/nombre/orden/
 * num_productos/estado) porque sin ella el formulario de edición nunca tiene
 * el valor real: como no existe GET /categorias/{id}, el campo quedaba
 * siempre vacío al editar, y al guardar mandaba "" presente — que
 * CategoriaActualizarRequest (JsonNullable) interpreta como "bórrala",
 * borrando en silencio cualquier descripción real que la categoría ya
 * tuviera (ver INTEGRACION.md hallazgo 3.36).
 */
public record CategoriaResumen(Integer id, String icono, String nombre, String descripcion, Integer orden,
                                long numProductos, String estado) {
}
