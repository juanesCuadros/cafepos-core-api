package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/** grupos: puede venir vacio o ausente — un combo sin grupos es un estado valido (ver ComboService.crear). */
public record ComboCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,
        String imagen,

        @NotNull(message = "precio es obligatorio")
        @Positive(message = "precio debe ser mayor a 0")
        BigDecimal precio,

        String estado,

        @Valid
        List<ComboGrupoRequest> grupos) {
}
