/**
 * Reportes operativos (solo Jefe) — Módulo 12
 *
 * Módulo de aplicación (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este módulo.
 *   - application: casos de uso; orquesta la transacción y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementación JPA de los puertos del dominio.
 *
 * Comunicación con otros módulos: solo a través de las clases públicas
 * expuestas directamente en este paquete (la "API" del módulo) o vía
 * eventos de aplicación (org.springframework.context.ApplicationEvent)
 * cuando se tolera consistencia eventual. Nunca acceder directamente a
 * domain/application/infrastructure de otro módulo — el test
 * ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.reportes;
