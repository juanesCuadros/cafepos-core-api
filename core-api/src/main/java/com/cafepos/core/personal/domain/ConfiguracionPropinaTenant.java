package com.cafepos.core.personal.domain;

import java.math.BigDecimal;

/** configuracion_sistema.propina_destino/propina_pct_mesero del tenant actual — leido directo (RLS ya escopea 1 fila), mismo patron ya usado por inventario.VencimientoJpaRepository. */
public record ConfiguracionPropinaTenant(String propinaDestino, BigDecimal propinaPctMesero) {
}
