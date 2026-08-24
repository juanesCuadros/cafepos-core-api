package com.cafepos.core.caja.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion nativa de la query de resumen_por_metodo_pago — ver VentaPagoJpaRepository. */
public interface ResumenMetodoPagoRow {

    String getNombre();

    BigDecimal getTotal();
}
