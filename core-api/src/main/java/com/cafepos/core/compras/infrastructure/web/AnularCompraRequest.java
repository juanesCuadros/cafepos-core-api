package com.cafepos.core.compras.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

/** usuarioAutorizaId viaja por compatibilidad con el contrato — no se revalida, la autorizacion real ya la hizo PinStepUpService. */
public record AnularCompraRequest(
        @NotBlank(message = "motivo es obligatorio") String motivo,
        Integer usuarioAutorizaId) {
}
