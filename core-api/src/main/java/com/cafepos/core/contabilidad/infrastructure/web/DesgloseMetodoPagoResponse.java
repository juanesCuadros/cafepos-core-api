package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.ItemDesglose;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record DesgloseMetodoPagoResponse(String metodo, @Monto BigDecimal total) {

    public static DesgloseMetodoPagoResponse de(ItemDesglose item) {
        return new DesgloseMetodoPagoResponse(item.nombre(), item.total());
    }
}
