package com.cafepos.core.productosmenu.domain;

/** Referencia liviana a un producto (id+nombre) - usado en promocion, evita cargar la entidad completa. */
public record ProductoRef(Integer id, String nombre) {
}
