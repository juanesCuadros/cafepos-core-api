/**
 * Venta Rápida, POS (Cobro), Apertura/Cierre, Historial de caja, Historial
 * de ventas — Módulo 3 (Facturación y Devoluciones quedan para un prompt
 * futuro, ver DECISIONES YA TOMADAS).
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * Llama directamente (sincrono, misma transaccion) a
 * com.cafepos.core.operacion.application.PedidoService (resolver/cerrar
 * pedido, liberar mesa), com.cafepos.core.restaurante.application.{MetodoPagoService,
 * FacturacionDianService} (validar metodo de pago, reservar numero de
 * factura), com.cafepos.core.productosmenu.application.PromocionService
 * (validar promocion activa), com.cafepos.core.clientes.application.ClienteService
 * (validar cliente) y com.cafepos.core.configuracion.application.ConfiguracionSistemaService
 * (inc_porcentaje default) — todos exponen puntualmente lo que este modulo
 * necesita via @org.springframework.modulith.NamedInterface, ver sus
 * respectivos package-info.java. usuario/PinStepUpService/PermissionEvaluator
 * salen de com.cafepos.core.shared (OPEN, sin necesidad de NamedInterface).
 *
 * Nunca acceder directamente a domain/application/infrastructure de otro
 * módulo — el test ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.caja;
