package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.application.GastoVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** GET /gastos/{id} — detalle completo, incluido el comprobante. */
public record GastoDetalleResponse(Integer id, String codigo, LocalDate fecha, Integer categoriaGastoId,
                                    String categoria, String descripcion, @Monto BigDecimal monto,
                                    String metodoPago, String comprobanteImagen, String observaciones,
                                    String usuario) {

    public static GastoDetalleResponse de(GastoVista vista) {
        return new GastoDetalleResponse(vista.gasto().getId(), vista.gasto().getCodigo(), vista.gasto().getFecha(),
                vista.gasto().getCategoriaGastoId(), vista.categoriaNombre(), vista.gasto().getDescripcion(),
                vista.gasto().getMonto(), vista.gasto().getMetodoPago(), vista.gasto().getComprobanteImagen(),
                vista.gasto().getObservaciones(), vista.usuarioNombre());
    }
}
