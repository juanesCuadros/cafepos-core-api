package com.cafepos.core.compras.domain;

/**
 * Fila de GET /proveedores. contacto SI va en el listado (api_06_compras.md
 * 6.3 lo incluye en el ejemplo de respuesta) — faltaba en la query real,
 * el listado lo devolvia undefined y la columna "Contacto" de la tabla
 * quedaba siempre vacia (ver INTEGRACION.md hallazgo 3.20). correo/direccion
 * si estan ausentes del listado a proposito, solo GET /proveedores/{id}
 * los trae.
 */
public record ProveedorResumen(Integer id, String codigo, String nombre, String nit, String contacto,
                                String telefono, String estado) {
}
