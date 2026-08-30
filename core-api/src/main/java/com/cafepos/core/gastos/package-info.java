/**
 * Gastos operativos del negocio (arriendo, servicios, nomina) — Modulo 9
 *
 * Distinto de Compras (insumos) y de los egresos de caja del dia
 * (caja.apertura_cierre) — ver api_09_gastos.md. gasto.metodo_pago es
 * texto libre (VARCHAR(50), sin CHECK ni FK), no tiene relacion con la
 * tabla metodo_pago de restaurante/caja pese al nombre parecido.
 *
 * Modulo de aplicacion (Spring Modulith). Arquitectura hexagonal:
 *   - domain: entidades, agregados y puertos (interfaces) de este modulo.
 *   - application: casos de uso; orquesta la transaccion y publica eventos.
 *   - infrastructure.web: controllers (adaptador de entrada HTTP).
 *   - infrastructure.persistence: implementacion JPA de los puertos del dominio.
 *
 * Autocontenido — no llama a ningun otro modulo de negocio (usuario_id
 * viaja como Integer plano desde el JWT, sin necesitar una @NamedInterface
 * de shared.seguridad para nombres: el nombre del usuario se resuelve via
 * UsuarioRepository, que ya es OPEN por vivir en shared).
 *
 * Comunicacion con otros modulos: solo a traves de las clases publicas
 * expuestas directamente en este paquete (la "API" del modulo) o via
 * eventos de aplicacion cuando se tolera consistencia eventual. Nunca
 * acceder directamente a domain/application/infrastructure de otro
 * modulo — el test ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.gastos;
