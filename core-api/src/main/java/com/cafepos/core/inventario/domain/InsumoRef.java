package com.cafepos.core.inventario.domain;

/**
 * Referencia minima de un insumo — validar insumo_id y mostrar
 * codigo/nombre/unidad_medida en el detalle de una compra
 * (com.cafepos.core.compras), nunca la entidad Insumo completa.
 *
 * @NamedInterface propio, ver InsumoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("insumoRef")
public record InsumoRef(Integer id, String codigo, String nombre, String unidadMedida) {
}
