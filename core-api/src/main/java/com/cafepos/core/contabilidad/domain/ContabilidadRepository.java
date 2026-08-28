package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de solo lectura sobre tablas de otros modulos (venta, compra,
 * gasto, caja_movimiento, caja_jornada y sus catalogos) — implementado
 * en infrastructure.persistence con SQL nativo directo, ver
 * package-info.java de este modulo.
 */
public interface ContabilidadRepository {

    BigDecimal totalVentasCobradas(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal totalComprasNoAnuladas(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal totalComprasPagadas(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal totalGastos(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal totalCajaMovimiento(String tipo, LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal totalVentasPorMetodoEfectivo(boolean esEfectivo, LocalDate fechaInicio, LocalDate fechaFin);

    List<ItemDesglose> desgloseIngresosPorMetodoPago(LocalDate fechaInicio, LocalDate fechaFin);

    List<ItemDesglose> desgloseComprasPorProveedor(LocalDate fechaInicio, LocalDate fechaFin);

    List<ItemDesglose> desgloseGastosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin);

    List<AperturaJornada> listarAperturasEnRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<VentaContable> listarVentasCobradasEnRango(LocalDate fechaInicio, LocalDate fechaFin,
                                                     Integer metodoPagoId);

    List<CompraContable> listarComprasPagadasEnRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<GastoContable> listarGastosEnRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<CajaMovimientoContable> listarCajaMovimientoEnRango(String tipo, LocalDate fechaInicio, LocalDate fechaFin);
}
