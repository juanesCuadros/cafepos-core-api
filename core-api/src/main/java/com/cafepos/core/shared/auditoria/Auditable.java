package com.cafepos.core.shared.auditoria;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un metodo de un @Service (ya @Transactional) para que
 * AuditoriaAspect registre una fila en evento_auditoria al terminar
 * exitosamente. entidadIdExpression es SpEL evaluado sobre los argumentos
 * del metodo anotado (ej. "#ventaId", con el nombre real del parametro).
 *
 * Prueba de concepto: solo instrumenta HistorialVentasService.anular() por
 * ahora (ver CLAUDE.md / prompt original) — no aplicar a otros metodos
 * todavia sin decidirlo explicitamente.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {

    String entidadTipo();

    String accion();

    String entidadIdExpression();
}
