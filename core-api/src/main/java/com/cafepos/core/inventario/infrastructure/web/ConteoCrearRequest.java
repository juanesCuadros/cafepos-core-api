package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ConteoCrearRequest(
        @NotEmpty(message = "detalle no puede estar vacio")
        @Valid
        List<ConteoDetalleRequest> detalle) {
}
