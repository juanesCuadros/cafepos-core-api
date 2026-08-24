package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.shared.seguridad.Usuario;

import java.math.BigDecimal;
import java.util.List;

/** Ver CajaJornadaService.actual — GET /caja/jornada/actual. */
public record JornadaActualVista(CajaJornada jornada, Usuario usuarioApertura, BigDecimal totalVentasActual,
                                  List<MovimientoVista> movimientos) {
}
