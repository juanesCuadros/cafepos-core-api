/**
 * Panel de mesas, Pedido abierto, Cocina (KDS) y Registrar/Cerrar turno —
 * Módulo 2.
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * Llama directamente (sincrono, misma transaccion) a
 * com.cafepos.core.restaurante.application.ZonaService (mesa/zona),
 * com.cafepos.core.productosmenu.application.{ProductoService,ComboService,
 * PromocionService} (agregar items, evaluar promociones_sugeridas) y
 * com.cafepos.core.configuracion.application.ConfiguracionSistemaService
 * (modo_comanda) — todos exponen puntualmente lo que este modulo necesita
 * via @org.springframework.modulith.NamedInterface, ver sus respectivos
 * package-info.java. usuario/PinStepUpService salen de com.cafepos.core.shared
 * (OPEN, sin necesidad de NamedInterface).
 *
 * Nunca acceder directamente a domain/application/infrastructure de otro
 * módulo — el test ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.operacion;
