package com.cafepos.core.productosmenu.domain;

import java.util.List;

/** Grupo con sus productos anidados — forma de "grupos" en la respuesta de combo (ver ComboRepository.gruposDe). */
public record ComboGrupoDetalle(Integer id, String nombre, List<ProductoRef> productos) {
}
