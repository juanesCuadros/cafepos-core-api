package com.cafepos.core.gastos.application;

import com.cafepos.core.gastos.domain.Gasto;

/** GET /gastos/{id} — categoriaNombre/usuarioNombre aplanados, mismo criterio que GastoResumen. */
public record GastoVista(Gasto gasto, String categoriaNombre, String usuarioNombre) {
}
