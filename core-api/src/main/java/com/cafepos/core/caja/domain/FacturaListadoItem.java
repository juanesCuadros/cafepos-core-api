package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila de GET /facturas — join factura_dian + venta + cliente, ver FacturaDianRepository.listar. */
public record FacturaListadoItem(Integer id, String numeroFactura, OffsetDateTime fechaEmision, String cliente,
                                  BigDecimal total, String estadoDian) {
}
