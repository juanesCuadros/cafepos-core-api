package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;

/** Fila de GET /clientes — numeroDocumentoEnmascarado ya viene enmascarado, nunca el documento completo. */
public record ClienteResumen(Integer id, String codigo, String nombre, String tipoDocumento,
                              String numeroDocumentoEnmascarado, String telefono, String correo,
                              BigDecimal saldoFavor) {
}
