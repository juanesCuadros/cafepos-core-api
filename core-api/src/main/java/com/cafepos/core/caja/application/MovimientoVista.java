package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaMovimiento;

/** Un CajaMovimiento con el nombre de usuario ya resuelto — ver CajaJornadaService. */
public record MovimientoVista(CajaMovimiento movimiento, String usuarioNombre) {
}
