package com.cafepos.core.shared.excepciones;

/** Como ErrorResponse, pero con un "codigo" adicional que el frontend puede matchear sin parsear el mensaje. */
public record ErrorConCodigoResponse(String error, String codigo) {
}
