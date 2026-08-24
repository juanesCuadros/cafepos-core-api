package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.ClienteNoEncontradoException;
import com.cafepos.core.caja.domain.DevolucionNoEncontradaException;
import com.cafepos.core.caja.domain.EstadoFacturaInvalidoException;
import com.cafepos.core.caja.domain.FacturaNoEncontradaException;
import com.cafepos.core.caja.domain.JornadaNoAbiertaException;
import com.cafepos.core.caja.domain.JornadaNoEncontradaException;
import com.cafepos.core.caja.domain.JornadaYaAbiertaException;
import com.cafepos.core.caja.domain.MetodoPagoNoEncontradoException;
import com.cafepos.core.caja.domain.PagoNoCoincideException;
import com.cafepos.core.caja.domain.PedidoItemNoEncontradoException;
import com.cafepos.core.caja.domain.PedidoNoEncontradoException;
import com.cafepos.core.caja.domain.PedidoYaCerradoException;
import com.cafepos.core.caja.domain.PromocionNoEncontradaException;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Caja) — a proposito NO
 * viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN, no
 * debe importar clases de un modulo de negocio cerrado, ver
 * InventarioExceptionHandler/OperacionExceptionHandler para el mismo
 * razonamiento).
 */
@RestControllerAdvice(basePackageClasses = CajaExceptionHandler.class)
public class CajaExceptionHandler {

    @ExceptionHandler(JornadaYaAbiertaException.class)
    public ResponseEntity<ErrorResponse> handleJornadaYaAbierta(JornadaYaAbiertaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(JornadaNoAbiertaException.class)
    public ResponseEntity<ErrorResponse> handleJornadaNoAbierta(JornadaNoAbiertaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(JornadaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleJornadaNoEncontrada(JornadaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoNoEncontrado(PedidoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoYaCerradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoYaCerrado(PedidoYaCerradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PromocionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handlePromocionNoEncontrada(PromocionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MetodoPagoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleMetodoPagoNoEncontrado(MetodoPagoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PagoNoCoincideException.class)
    public ResponseEntity<ErrorResponse> handlePagoNoCoincide(PagoNoCoincideException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(VentaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleVentaNoEncontrada(VentaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FacturaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleFacturaNoEncontrada(FacturaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(EstadoFacturaInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleEstadoFacturaInvalido(EstadoFacturaInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DevolucionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleDevolucionNoEncontrada(DevolucionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoItemNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoItemNoEncontrado(PedidoItemNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
}
