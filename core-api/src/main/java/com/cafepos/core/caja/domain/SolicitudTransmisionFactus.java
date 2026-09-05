package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Body completo (ya en vocabulario Factus) para POST /v2/bills/validate —
 * armado por FacturaDianTransmisionService, consumido por FacturaDianTransmisorPort.
 * referenceCode es venta.codigo (unico por tenant, trazable).
 *
 * descuentoRatePercent: mismo porcentaje de descuento (subtotal, prorateado
 * uniforme entre items, ver VentaService) aplicado a CADA item al transmitir
 * - antes iba fijo en 0 sin importar si la venta tuvo descuento real, lo que
 * hacia que Factus calculara un total distinto al que realmente se cobro
 * (hallazgo real, 422 "La suma de todos los detalles de pago no es igual
 * al total de la factura"). pagos ya viene con la propina excluida y
 * escalado proporcionalmente entre metodos de pago (ver
 * FacturaDianTransmisionService.escalarPagosSinPropina) - la propina
 * voluntaria no es base gravable ante la DIAN.
 */
public record SolicitudTransmisionFactus(String referenceCode, ClienteTransmisionFactus cliente,
                                          List<ItemTransmisionFactus> items, List<PagoTransmisionFactus> pagos,
                                          BigDecimal descuentoRatePercent) {
}
