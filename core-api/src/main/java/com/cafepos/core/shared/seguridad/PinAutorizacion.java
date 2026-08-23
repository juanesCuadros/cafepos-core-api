package com.cafepos.core.shared.seguridad;

/** Resultado de PinVerificarService.ejecutar en el camino exitoso — ver PinVerificarResponse.de. */
public record PinAutorizacion(Integer usuarioAutorizaId, String pinToken) {
}
