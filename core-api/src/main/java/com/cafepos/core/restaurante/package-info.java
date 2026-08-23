/**
 * Información general, Zonas y Mesas, Métodos de pago, Facturación DIAN
 * (solo lectura) y Menú digital (QR) — Módulo 10.
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP), incluye
 *     el endpoint público GET /menu-publico (sin JWT, ver MenuPublicoController).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * El menú público (MenuPublicoService) llama directamente a
 * com.cafepos.core.productosmenu.application.ProductoService — "llamada
 * directa síncrona" entre módulos, sancionada cuando se necesita el dato
 * en el mismo request (ver package-info de productosmenu). Nunca acceder
 * directamente a domain/application/infrastructure de otro módulo — el
 * test ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.restaurante;
