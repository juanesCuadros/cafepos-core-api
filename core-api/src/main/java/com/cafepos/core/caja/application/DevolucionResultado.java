package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Devolucion;

/** detalle explica que regla determino el metodo_reembolso — ver DevolucionService.solicitar. */
public record DevolucionResultado(Devolucion devolucion, boolean notaCreditoGenerada, Integer notaCreditoId,
                                   String detalle) {
}
