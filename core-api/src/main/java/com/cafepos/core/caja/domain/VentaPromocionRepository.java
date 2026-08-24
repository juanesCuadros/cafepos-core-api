package com.cafepos.core.caja.domain;

/** Puerto de persistencia de VentaPromocion — implementado en infrastructure.persistence. */
public interface VentaPromocionRepository {

    VentaPromocion guardar(VentaPromocion ventaPromocion);
}
