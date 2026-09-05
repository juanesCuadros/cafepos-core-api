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
 * al total de la factura").
 *
 * propina/baseImponiblePropina: Factus SI contempla la propina, como recargo
 * a nivel de factura (allowance_charges, concept_type "03") — no como item
 * ni excluida del todo (ver FactusFacturacionClienteAdapter). propina en
 * cero/null no agrega ningun allowance_charge.
 */
public record SolicitudTransmisionFactus(String referenceCode, ClienteTransmisionFactus cliente,
                                          List<ItemTransmisionFactus> items, List<PagoTransmisionFactus> pagos,
                                          BigDecimal descuentoRatePercent, BigDecimal propina,
                                          BigDecimal baseImponiblePropina) {
}
