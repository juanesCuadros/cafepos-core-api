package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.caja.domain.ResumenMetodoPago;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista completa de una jornada (cierre/arqueo, o detalle de historial) —
 * ver CajaJornadaService.cerrar y CajaHistorialService.detalle. usuarioCierreNombre
 * es null si la jornada sigue abierta. resumenPorMetodoPago/movimientos
 * vienen vacios en el listado (GET /caja/jornadas), solo se llenan en el
 * detalle/cierre puntual.
 */
public record JornadaArqueoVista(CajaJornada jornada, String usuarioAperturaNombre, String usuarioCierreNombre,
                                  BigDecimal totalVentas, List<ResumenMetodoPago> resumenPorMetodoPago,
                                  List<MovimientoVista> movimientos) {
}
