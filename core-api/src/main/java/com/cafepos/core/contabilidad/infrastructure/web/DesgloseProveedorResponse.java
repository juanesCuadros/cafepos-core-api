package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.ItemDesglose;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record DesgloseProveedorResponse(String proveedor, @Monto BigDecimal total) {

    public static DesgloseProveedorResponse de(ItemDesglose item) {
        return new DesgloseProveedorResponse(item.nombre(), item.total());
    }
}
