package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

/** usuarioAutorizaId viaja por compatibilidad con el contrato — no se revalida, la autorizacion real ya la hizo PinStepUpService. */
public record AnularVentaRequest(
        @NotBlank(message = "motivo es obligatorio") String motivo,
        Integer usuarioAutorizaId) {
}
