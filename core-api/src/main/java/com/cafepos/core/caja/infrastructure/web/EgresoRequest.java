package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * usuarioAutorizaId viaja en el body por compatibilidad con el contrato
 * (api_03_caja.md), pero NO se revalida aca — la autorizacion real ya la
 * hizo PinStepUpService.validar contra el header X-Pin-Token (ver
 * CajaJornadaController.egreso). Tampoco se persiste — caja_movimiento no
 * tiene columna separada para esto, usuario_id ya es quien ejecuta la
 * accion (el cajero logueado).
 */
public record EgresoRequest(
        @NotNull(message = "monto es obligatorio")
        @Positive(message = "monto debe ser mayor a 0")
        BigDecimal monto,

        @NotBlank(message = "motivo es obligatorio")
        String motivo,

        Integer usuarioAutorizaId) {
}
