package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.CedulaDuplicadaException;
import com.cafepos.core.personal.domain.EmpleadoNoEncontradoException;
import com.cafepos.core.personal.domain.HoraFinAntesDeInicioException;
import com.cafepos.core.personal.domain.TurnoNoEncontradoException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Personal) — a proposito
 * NO viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN,
 * no debe importar clases de un modulo de negocio cerrado, ver
 * InventarioExceptionHandler/ComprasExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = PersonalExceptionHandler.class)
public class PersonalExceptionHandler {

    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleEmpleadoNoEncontrado(EmpleadoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CedulaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleCedulaDuplicada(CedulaDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TurnoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTurnoNoEncontrado(TurnoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(HoraFinAntesDeInicioException.class)
    public ResponseEntity<ErrorResponse> handleHoraFinAntesDeInicio(HoraFinAntesDeInicioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
