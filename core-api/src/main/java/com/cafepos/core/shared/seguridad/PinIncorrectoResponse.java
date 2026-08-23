package com.cafepos.core.shared.seguridad;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Cuerpo de PinIncorrectoException via GlobalExceptionHandler. intentosRestantes
 * se omite del JSON cuando es null (caso generico de usuario_autoriza_correo
 * inexistente/rol invalido/sin PIN configurado, ver PinIncorrectoException).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PinIncorrectoResponse(boolean autorizado, String error, String codigo, Integer intentosRestantes) {
}
