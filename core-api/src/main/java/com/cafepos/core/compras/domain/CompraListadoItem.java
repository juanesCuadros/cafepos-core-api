package com.cafepos.core.compras.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de GET /compras — proveedor_nombre/usuario_nombre vienen aplanados, el join ya se hizo en SQL. */
public record CompraListadoItem(Integer id, String codigo, LocalDate fecha, Integer proveedorId,
                                 String proveedorNombre, Integer usuarioId, String usuarioNombre, String formaPago,
                                 String estado, BigDecimal total) {
}
