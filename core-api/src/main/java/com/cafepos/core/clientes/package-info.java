/**
 * Clientes — Módulo 7. Vista única (sin sub-vistas): el mismo formulario
 * se reutiliza como modal de registro rápido en el POS.
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * Nunca acceder directamente a domain/application/infrastructure de otro
 * módulo — el test ModularityTests falla el build si esto ocurre. Si
 * este módulo necesita llamar sincrónicamente a otro (ver Arquitectura
 * del proyecto en CLAUDE.md), exponer solo lo puntual con
 * @org.springframework.modulith.NamedInterface, nunca el paquete entero.
 */
package com.cafepos.core.clientes;
