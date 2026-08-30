package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.FormatoNoDisponibleException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Excepciones de dominio propias de este modulo (Contabilidad) — no viven en shared.excepciones.GlobalExceptionHandler, ver InventarioExceptionHandler para el mismo razonamiento. */
@RestControllerAdvice(basePackageClasses = ContabilidadExceptionHandler.class)
public class ContabilidadExceptionHandler {

    @ExceptionHandler(FormatoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleFormatoNoDisponible(FormatoNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ErrorResponse(ex.getMessage()));
    }
}
