package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;

/**
 * Fila de GET /clientes/buscar — version liviana para el modal de
 * seleccion de cliente en el POS (ver api_03_caja.md 3.2). Campos
 * verificados contra el contrato: id, tipoDocumento,
 * numeroDocumentoEnmascarado, nombre, saldoFavor — sin telefono/correo,
 * que no aportan al flujo de elegir cliente en una venta.
 */
public record ClienteBusqueda(Integer id, String nombre, String tipoDocumento, String numeroDocumentoEnmascarado,
                               BigDecimal saldoFavor) {
}
