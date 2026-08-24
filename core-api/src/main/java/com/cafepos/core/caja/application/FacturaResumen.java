package com.cafepos.core.caja.application;

/** Ver VentaResultado.factura — null si cliente_id es null o el tenant no tiene resolucion DIAN configurada. */
public record FacturaResumen(Integer id, String numeroFactura, String estadoDian) {
}
