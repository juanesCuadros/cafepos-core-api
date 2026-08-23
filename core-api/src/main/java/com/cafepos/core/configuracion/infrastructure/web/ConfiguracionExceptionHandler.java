package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.AreaCocinaNoEncontradaException;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaNoConfiguradaException;
import com.cafepos.core.configuracion.domain.ImpresoraConexionInvalidaException;
import com.cafepos.core.configuracion.domain.ImpresoraNoEncontradaException;
import com.cafepos.core.configuracion.domain.PinNoPermitidoException;
import com.cafepos.core.configuracion.domain.RolJefeNoEditableException;
import com.cafepos.core.configuracion.domain.RolNoEncontradoException;
import com.cafepos.core.configuracion.domain.UsuarioCorreoDuplicadoException;
import com.cafepos.core.configuracion.domain.UsuarioNoEncontradoException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Configuracion) — a
 * proposito NO viven en shared.excepciones.GlobalExceptionHandler (shared
 * es OPEN, no debe importar clases de un modulo de negocio cerrado, ver
 * ProductosMenuExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = ConfiguracionExceptionHandler.class)
public class ConfiguracionExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioCorreoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleCorreoDuplicado(UsuarioCorreoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PinNoPermitidoException.class)
    public ResponseEntity<ErrorResponse> handlePinNoPermitido(PinNoPermitidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRolNoEncontrado(RolNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolJefeNoEditableException.class)
    public ResponseEntity<ErrorResponse> handleRolJefeNoEditable(RolJefeNoEditableException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConfiguracionSistemaNoConfiguradaException.class)
    public ResponseEntity<ErrorResponse> handleConfiguracionSistemaNoConfigurada(
            ConfiguracionSistemaNoConfiguradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AreaCocinaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleAreaCocinaNoEncontrada(AreaCocinaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ImpresoraNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleImpresoraNoEncontrada(ImpresoraNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ImpresoraConexionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleImpresoraConexionInvalida(ImpresoraConexionInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
