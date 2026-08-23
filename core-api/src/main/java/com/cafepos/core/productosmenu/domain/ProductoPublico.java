package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/**
 * Fila para el menu publico (ver restaurante.application.MenuPublicoService)
 * — categoria aplanada (solo nombre+orden, sin id: el contrato publico no
 * expone ids internos), y ya filtrada por categoria.estado='activa' +
 * producto.estado='activo' + producto.visibilidad='visible'.
 *
 * @NamedInterface propio (no todo el paquete domain): es el UNICO tipo de
 * este paquete expuesto a otros modulos, ver ProductoService (tambien
 * anotado) y package-info.java de este modulo.
 */
@org.springframework.modulith.NamedInterface("productoPublico")
public record ProductoPublico(String categoriaNombre, Integer categoriaOrden, String nombre, String descripcion,
                               BigDecimal precioVenta, String imagen) {
}
