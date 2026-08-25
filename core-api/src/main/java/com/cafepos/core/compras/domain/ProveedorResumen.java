package com.cafepos.core.compras.domain;

/** Fila de GET /proveedores. */
public record ProveedorResumen(Integer id, String codigo, String nombre, String nit, String telefono,
                                String estado) {
}
