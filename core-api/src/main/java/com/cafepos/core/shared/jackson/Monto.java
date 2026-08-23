package com.cafepos.core.shared.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un campo BigDecimal de un DTO de respuesta como monto de dinero —
 * se serializa SIEMPRE con 2 decimales fijos (ver MontoSerializer), sin
 * importar la escala interna que traiga (columna DECIMAL(x,2) leida
 * directa, o el resultado de una multiplicacion/suma en codigo que infla
 * la escala). Un solo lugar para este criterio en vez de repetir
 * setScale(2, ...) campo por campo en cada DTO — reusar en cualquier
 * modulo nuevo que maneje dinero (Compras, Gastos, Contabilidad, ...).
 *
 * NUNCA usar en un campo de CANTIDAD (stock, cantidad de un item,
 * capacidad, diferencia de conteo, etc.) — esos son DECIMAL(12,3) a
 * proposito y perderian precision real si se fuerzan a 2 decimales.
 *
 * Uso: {@code @Monto BigDecimal subtotal} directo en el record component
 * de cualquier DTO de infrastructure.web (funciona igual en record y en
 * clase normal).
 */
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = MontoSerializer.class)
public @interface Monto {
}
