package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.AreaCocinaNoEncontradaException;
import com.cafepos.core.productosmenu.domain.CategoriaConProductosException;
import com.cafepos.core.productosmenu.domain.CategoriaNoEncontradaException;
import com.cafepos.core.productosmenu.domain.ComboGrupoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ComboGrupoProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ComboGrupoProductoYaExisteException;
import com.cafepos.core.productosmenu.domain.ComboNoEncontradoException;
import com.cafepos.core.productosmenu.domain.DiaSemanaInvalidoException;
import com.cafepos.core.productosmenu.domain.ProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ProductosIdsRequeridosException;
import com.cafepos.core.productosmenu.domain.PromocionNoEncontradaException;
import com.cafepos.core.productosmenu.domain.ValorDescuentoInvalidoException;
import com.cafepos.core.productosmenu.domain.VigenciaInvalidaException;
import com.cafepos.core.shared.excepciones.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Excepciones de dominio propias de este modulo (Productos y Menu) — a
 * proposito NO viven en shared.excepciones.GlobalExceptionHandler: ese
 * handler es del modulo shared (OPEN), y shared no debe importar clases de
 * un modulo de negocio cerrado como productosmenu (rompe el sentido de la
 * direccion de dependencia que ModularityTests protege). basePackageClasses
 * scopea este advice solo a los controllers de este paquete, para que los
 * proximos controllers del modulo (Productos, Combos, Recetas,
 * Promociones) queden cubiertos automaticamente sin tocar esta clase.
 */
@RestControllerAdvice(basePackageClasses = ProductosMenuExceptionHandler.class)
public class ProductosMenuExceptionHandler {

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaNoEncontrada(CategoriaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CategoriaConProductosException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaConProductos(CategoriaConProductosException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AreaCocinaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleAreaCocinaNoEncontrada(AreaCocinaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PromocionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handlePromocionNoEncontrada(PromocionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ValorDescuentoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleValorDescuentoInvalido(ValorDescuentoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(VigenciaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleVigenciaInvalida(VigenciaInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductosIdsRequeridosException.class)
    public ResponseEntity<ErrorResponse> handleProductosIdsRequeridos(ProductosIdsRequeridosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DiaSemanaInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleDiaSemanaInvalido(DiaSemanaInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleComboNoEncontrado(ComboNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboGrupoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleComboGrupoNoEncontrado(ComboGrupoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboGrupoProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleComboGrupoProductoNoEncontrado(ComboGrupoProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ComboGrupoProductoYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleComboGrupoProductoYaExiste(ComboGrupoProductoYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }
}
