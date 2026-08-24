package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.domain.ComboNoEncontradoException;
import com.cafepos.core.operacion.domain.ComboSeleccionIncompletaException;
import com.cafepos.core.operacion.domain.ItemInvalidoException;
import com.cafepos.core.operacion.domain.MesaDestinoNoDisponibleException;
import com.cafepos.core.operacion.domain.MesaIdNoPermitidoException;
import com.cafepos.core.operacion.domain.MesaIdObligatorioException;
import com.cafepos.core.operacion.domain.MesaNoEncontradaException;
import com.cafepos.core.operacion.domain.MesaOcupadaException;
import com.cafepos.core.operacion.domain.PedidoItemNoEncontradoException;
import com.cafepos.core.operacion.domain.PedidoNoEncontradoException;
import com.cafepos.core.operacion.domain.PedidoSinMesaException;
import com.cafepos.core.operacion.domain.ProductoAgotadoException;
import com.cafepos.core.operacion.domain.ProductoNoEncontradoException;
import com.cafepos.core.operacion.domain.TransicionEstadoInvalidaException;
import com.cafepos.core.operacion.domain.TurnoNoActivoException;
import com.cafepos.core.operacion.domain.TurnoYaActivoException;
import com.cafepos.core.operacion.domain.UsuarioSinEmpleadoException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Operacion) — a proposito
 * NO viven en shared.excepciones.GlobalExceptionHandler (shared es OPEN, no
 * debe importar clases de un modulo de negocio cerrado, ver
 * InventarioExceptionHandler para el mismo razonamiento).
 */
@RestControllerAdvice(basePackageClasses = OperacionExceptionHandler.class)
public class OperacionExceptionHandler {

    @ExceptionHandler(MesaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleMesaNoEncontrada(MesaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesaOcupadaException.class)
    public ResponseEntity<ErrorResponse> handleMesaOcupada(MesaOcupadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesaDestinoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleMesaDestinoNoDisponible(MesaDestinoNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoNoEncontrado(PedidoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoItemNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoItemNoEncontrado(PedidoItemNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductoAgotadoException.class)
    public ResponseEntity<ErrorResponse> handleProductoAgotado(ProductoAgotadoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleComboNoEncontrado(ComboNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboSeleccionIncompletaException.class)
    public ResponseEntity<ErrorResponse> handleComboSeleccionIncompleta(ComboSeleccionIncompletaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ItemInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleItemInvalido(ItemInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionEstadoInvalida(TransicionEstadoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TurnoYaActivoException.class)
    public ResponseEntity<ErrorResponse> handleTurnoYaActivo(TurnoYaActivoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TurnoNoActivoException.class)
    public ResponseEntity<ErrorResponse> handleTurnoNoActivo(TurnoNoActivoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioSinEmpleadoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioSinEmpleado(UsuarioSinEmpleadoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesaIdObligatorioException.class)
    public ResponseEntity<ErrorResponse> handleMesaIdObligatorio(MesaIdObligatorioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesaIdNoPermitidoException.class)
    public ResponseEntity<ErrorResponse> handleMesaIdNoPermitido(MesaIdNoPermitidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PedidoSinMesaException.class)
    public ResponseEntity<ErrorResponse> handlePedidoSinMesa(PedidoSinMesaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
