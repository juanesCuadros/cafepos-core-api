/**
 * Registro de compras, historial y proveedores — Módulo 6
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * Llama directamente (sincrono, misma transaccion) a
 * com.cafepos.core.inventario.application.{InsumoService,LoteInsumoService,
 * MovimientoInventarioService} — todos NamedInterface, para sumar/revertir
 * stock, sobreescribir costo_actual, generar/agotar lotes y registrar
 * movimientos de entrada/salida al registrar/anular una compra (ver
 * CompraService). El resto (domain/application/infrastructure interno de
 * inventario) sigue cerrado.
 *
 * Comunicación con otros módulos: solo a través de las clases públicas
 * expuestas directamente en este paquete (la "API" del módulo) o vía
 * eventos de aplicación (org.springframework.context.ApplicationEvent)
 * cuando se tolera consistencia eventual. Nunca acceder directamente a
 * domain/application/infrastructure de otro módulo — el test
 * ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.compras;
