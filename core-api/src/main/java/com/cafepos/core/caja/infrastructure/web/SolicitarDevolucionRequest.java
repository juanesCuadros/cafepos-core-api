package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** usuarioAutorizaId no se revalida contra el caller — la autorizacion real ya la hizo PinStepUpService. */
public record SolicitarDevolucionRequest(
        @NotNull(message = "ventaId es obligatorio") Integer ventaId,
        @NotEmpty(message = "items no puede estar vacio") List<@Valid ItemDevolucionRequest> items,
        @NotBlank(message = "motivo es obligatorio") String motivo,
        @NotNull(message = "usuarioAutorizaId es obligatorio") Integer usuarioAutorizaId) {
}
