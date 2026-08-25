package com.cafepos.core.compras.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record CompraCrearRequest(
        @NotNull(message = "proveedor_id es obligatorio") Integer proveedorId,
        String numeroFacturaProv,
        @NotNull(message = "fecha es obligatoria") LocalDate fecha,
        @Pattern(regexp = "contado|credito", message = "forma_pago debe ser 'contado' o 'credito'")
        String formaPago,
        String observaciones,
        @NotEmpty(message = "el detalle no puede estar vacio")
        @Valid List<CompraDetalleInputRequest> detalle) {
}
