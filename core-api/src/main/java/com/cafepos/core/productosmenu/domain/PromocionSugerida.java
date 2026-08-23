package com.cafepos.core.productosmenu.domain;

import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

/** @NamedInterface propio, ver ItemParaPromocion / PromocionService.evaluarSugeridas. */
@org.springframework.modulith.NamedInterface("promocionSugerida")
public record PromocionSugerida(Integer promocionId, String nombre, @Monto BigDecimal montoDescuentoEstimado) {
}
