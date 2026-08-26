package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.CategoriaGastoInactivaException;
import com.cafepos.core.gastos.domain.CategoriaGastoNoEncontradaException;
import com.cafepos.core.gastos.domain.GastoNoEncontradoException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Excepciones de dominio propias de este modulo (Gastos) — no viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN, ver InventarioExceptionHandler para el mismo razonamiento). */
@RestControllerAdvice(basePackageClasses = GastosExceptionHandler.class)
public class GastosExceptionHandler {

    @ExceptionHandler(CategoriaGastoNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaGastoNoEncontrada(CategoriaGastoNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CategoriaGastoInactivaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaGastoInactiva(CategoriaGastoInactivaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(GastoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleGastoNoEncontrado(GastoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
}
