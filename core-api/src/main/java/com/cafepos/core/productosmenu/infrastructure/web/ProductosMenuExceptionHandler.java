package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.CategoriaConProductosException;
import com.cafepos.core.productosmenu.domain.CategoriaNoEncontradaException;
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
}
