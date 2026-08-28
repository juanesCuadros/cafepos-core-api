/**
 * Contabilidad — Balance general, Flujo de caja, Transacciones (Modulo 13)
 *
 * Modulo de solo lectura, exclusivo del rol Jefe (ver catalogo permiso:
 * contabilidad.balance_general / contabilidad.flujo_caja /
 * contabilidad.transacciones, accion "ver"). No tiene entidades propias
 * ni tablas propias — agrega datos de venta/venta_pago/metodo_pago
 * (Caja), compra/proveedor (Compras), gasto/categoria_gasto (Gastos) y
 * caja_movimiento/caja_jornada (Caja), todos leidos por SQL nativo
 * directo contra las tablas fisicas (Modulith no restringe el acceso a
 * tablas via SQL nativo, solo el acceso a clases Java de otro modulo —
 * mismo patron ya usado en personal.PropinaJpaRepository e
 * inventario.VencimientoJpaRepository).
 *
 * Arquitectura hexagonal:
 *   - domain: puertos y records de este modulo (no hay entidades JPA
 *     propias — ver ContabilidadJpaRepository, parametrizado sobre
 *     shared.seguridad.Usuario solo porque TenantAwareRepository exige
 *     un tipo de entidad, mismo patron que los ejemplos de arriba).
 *   - application: casos de uso (calculo de balance/flujo/transacciones,
 *     resolucion de rango de fechas segun "vista").
 *   - infrastructure.web: controllers.
 *   - infrastructure.persistence: adapter con las queries nativas.
 *
 * Comunicación con otros módulos: ninguna via Java — solo SQL nativo
 * contra tablas de otros modulos. Nunca acceder directamente a
 * domain/application/infrastructure de otro modulo — el test
 * ModularityTests falla el build si esto ocurre.
 */
package com.cafepos.core.contabilidad;
