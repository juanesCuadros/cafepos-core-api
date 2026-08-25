package com.cafepos.core.caja.domain;

import java.util.List;

/**
 * Body completo (ya en vocabulario Factus) para POST /v2/bills/validate —
 * armado por FacturaDianTransmisionService, consumido por FacturaDianTransmisorPort.
 * referenceCode es venta.codigo (unico por tenant, trazable).
 */
public record SolicitudTransmisionFactus(String referenceCode, ClienteTransmisionFactus cliente,
                                          List<ItemTransmisionFactus> items, List<PagoTransmisionFactus> pagos) {
}
