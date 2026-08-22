package com.cafepos.core.shared.excepciones;

/**
 * Forma unica de respuesta de error de toda la API — ver GlobalExceptionHandler y FilterErrorWriter.
 * Clave "error" (no "mensaje") siguiendo el contrato (api_00_autenticacion.md) — "mensaje" queda
 * reservado para respuestas de EXITO sin datos (ej. LogoutResponse).
 */
public record ErrorResponse(String error) {
}
