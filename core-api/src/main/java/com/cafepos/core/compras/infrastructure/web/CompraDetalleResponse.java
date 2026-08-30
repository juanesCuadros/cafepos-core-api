package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.application.CompraVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** GET /compras/{id} — misma forma que el POST, mas los campos generados (id, codigo, estado, proveedor_nombre, usuario_nombre). */
public record CompraDetalleResponse(Integer id, String codigo, Integer proveedorId, String proveedorNombre,
                                     Integer usuarioId, String usuarioNombre, String numeroFacturaProv,
                                     LocalDate fecha, String formaPago, String estado, String observaciones,
                                     String motivoAnulacion, @Monto BigDecimal total,
                                     List<CompraDetalleItemResponse> detalle) {

    public static CompraDetalleResponse de(CompraVista vista) {
        var c = vista.compra();
        return new CompraDetalleResponse(c.getId(), c.getCodigo(), c.getProveedorId(), vista.proveedorNombre(),
                c.getUsuarioId(), vista.usuarioNombre(), c.getNumeroFacturaProv(), c.getFecha(), c.getFormaPago(),
                c.getEstado(), c.getObservaciones(), c.getMotivoAnulacion(), c.getTotal(),
                vista.items().stream().map(CompraDetalleItemResponse::de).toList());
    }
}
