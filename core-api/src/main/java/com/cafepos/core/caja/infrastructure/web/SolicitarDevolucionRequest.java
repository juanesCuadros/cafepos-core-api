package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * usuarioAutorizaId no se revalida contra el caller — la autorizacion real ya
 * la hizo PinStepUpService. Opcional a proposito: cuando confirma un Jefe (no
 * pasa por el PIN de un tercero, es el Jefe mismo autenticado), el frontend
 * no manda este campo — DevolucionController.solicitar resuelve el usuario
 * autenticado del token como autorizador en ese caso, igual que ya hace
 * CajaJornadaController para abrir jornada/registrar ingreso.
 */
public record SolicitarDevolucionRequest(
        @NotNull(message = "ventaId es obligatorio") Integer ventaId,
        @NotEmpty(message = "items no puede estar vacio") List<@Valid ItemDevolucionRequest> items,
        @NotBlank(message = "motivo es obligatorio") String motivo,
        Integer usuarioAutorizaId) {
}
