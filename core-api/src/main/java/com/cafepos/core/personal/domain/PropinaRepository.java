package com.cafepos.core.personal.domain;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Puerto de solo lectura para el calculo de propinas atribuidas — lee
 * venta/pedido/configuracion_sistema directo por nombre de tabla (no son
 * entidades de este modulo, mismo patron ya usado en FacturaDianJpaRepository
 * uniendo venta/cliente desde el modulo restaurante) — Modulith solo
 * restringe acceso a nivel de clase Java, no a joins SQL de solo lectura.
 */
public interface PropinaRepository {

    ConfiguracionPropinaTenant obtenerConfiguracionPropina();

    /** desde/hasta pueden venir null (sin filtrar por fecha). */
    List<VentaConPropina> listarVentasConPropina(Integer usuarioId, OffsetDateTime desde, OffsetDateTime hasta);
}
