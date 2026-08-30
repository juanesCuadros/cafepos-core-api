package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.ItemDesglose;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record DesgloseCategoriaResponse(String categoria, @Monto BigDecimal total) {

    public static DesgloseCategoriaResponse de(ItemDesglose item) {
        return new DesgloseCategoriaResponse(item.nombre(), item.total());
    }
}
