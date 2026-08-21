package com.cafepos.core.shared.excepciones;

/** Forma unica de respuesta de error de toda la API — ver GlobalExceptionHandler y FilterErrorWriter. */
public record ErrorResponse(String mensaje) {
}
