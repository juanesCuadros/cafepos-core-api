/**
 * Existencias, Insumos, Historial de movimientos, Conteos, Pérdidas y
 * Vencimientos — Módulo 5.
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
package com.cafepos.core.inventario;
