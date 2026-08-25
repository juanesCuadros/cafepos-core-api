package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.CompraAnuladaBloqueadaException;
import com.cafepos.core.compras.domain.CompraNoEncontradaException;
import com.cafepos.core.compras.domain.CompraNoMarcablePagadaException;
import com.cafepos.core.compras.domain.InsumoInvalidoException;
import com.cafepos.core.compras.domain.ProveedorConComprasException;
import com.cafepos.core.compras.domain.ProveedorNoEncontradoException;
import com.cafepos.core.compras.domain.StockInsuficienteParaAnularException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Compras) — a proposito NO
 * viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN, no
 * debe importar clases de un modulo de negocio cerrado, ver
 * InventarioExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = ComprasExceptionHandler.class)
public class ComprasExceptionHandler {

    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProveedorNoEncontrado(ProveedorNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProveedorConComprasException.class)
    public ResponseEntity<ErrorResponse> handleProveedorConCompras(ProveedorConComprasException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CompraNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCompraNoEncontrada(CompraNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InsumoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleInsumoInvalido(InsumoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CompraAnuladaBloqueadaException.class)
    public ResponseEntity<ErrorResponse> handleCompraAnuladaBloqueada(CompraAnuladaBloqueadaException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteParaAnularException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficienteParaAnular(StockInsuficienteParaAnularException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CompraNoMarcablePagadaException.class)
    public ResponseEntity<ErrorResponse> handleCompraNoMarcablePagada(CompraNoMarcablePagadaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
