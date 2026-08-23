package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteConVentasException;
import com.cafepos.core.clientes.domain.ClienteDocumentoDuplicadoException;
import com.cafepos.core.clientes.domain.ClienteNoEliminableException;
import com.cafepos.core.clientes.domain.ClienteNoEncontradoException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Clientes) — a proposito
 * NO viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN,
 * no debe importar clases de un modulo de negocio cerrado, ver
 * ProductosMenuExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = ClientesExceptionHandler.class)
public class ClientesExceptionHandler {

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClienteDocumentoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDocumentoDuplicado(ClienteDocumentoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClienteConVentasException.class)
    public ResponseEntity<ErrorResponse> handleClienteConVentas(ClienteConVentasException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClienteNoEliminableException.class)
    public ResponseEntity<ErrorResponse> handleClienteNoEliminable(ClienteNoEliminableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }
}
