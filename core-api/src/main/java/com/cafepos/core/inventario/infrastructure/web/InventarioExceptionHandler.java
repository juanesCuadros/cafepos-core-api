package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.CategoriaInsumoNoEncontradaException;
import com.cafepos.core.inventario.domain.ConteoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.StockInsuficienteException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Inventario) — a
 * proposito NO viven en shared.excepciones.GlobalExceptionHandler (shared
 * es OPEN, no debe importar clases de un modulo de negocio cerrado, ver
 * ProductosMenuExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = InventarioExceptionHandler.class)
public class InventarioExceptionHandler {

    @ExceptionHandler(CategoriaInsumoNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaInsumoNoEncontrada(CategoriaInsumoNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InsumoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleInsumoNoEncontrado(InsumoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConteoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleConteoNoEncontrado(ConteoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
}
