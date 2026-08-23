package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MenuPublicoNoDisponibleException;
import com.cafepos.core.restaurante.domain.MesaNoEncontradaException;
import com.cafepos.core.restaurante.domain.MetodoPagoEfectivoNoDesactivableException;
import com.cafepos.core.restaurante.domain.MetodoPagoEfectivoNoEliminableException;
import com.cafepos.core.restaurante.domain.MetodoPagoNoEncontradoException;
import com.cafepos.core.restaurante.domain.RestauranteNoConfiguradoException;
import com.cafepos.core.restaurante.domain.ZonaConMesasException;
import com.cafepos.core.restaurante.domain.ZonaNoEncontradaException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Restaurante) — a
 * proposito NO viven en shared.excepciones.GlobalExceptionHandler (shared
 * es OPEN, no debe importar clases de un modulo de negocio cerrado, ver
 * ProductosMenuExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = RestauranteExceptionHandler.class)
public class RestauranteExceptionHandler {

    @ExceptionHandler(RestauranteNoConfiguradoException.class)
    public ResponseEntity<ErrorResponse> handleRestauranteNoConfigurado(RestauranteNoConfiguradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ZonaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleZonaNoEncontrada(ZonaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ZonaConMesasException.class)
    public ResponseEntity<ErrorResponse> handleZonaConMesas(ZonaConMesasException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleMesaNoEncontrada(MesaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MetodoPagoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleMetodoPagoNoEncontrado(MetodoPagoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MetodoPagoEfectivoNoDesactivableException.class)
    public ResponseEntity<ErrorResponse> handleEfectivoNoDesactivable(MetodoPagoEfectivoNoDesactivableException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MetodoPagoEfectivoNoEliminableException.class)
    public ResponseEntity<ErrorResponse> handleEfectivoNoEliminable(MetodoPagoEfectivoNoEliminableException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MenuPublicoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleMenuPublicoNoDisponible(MenuPublicoNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
}
