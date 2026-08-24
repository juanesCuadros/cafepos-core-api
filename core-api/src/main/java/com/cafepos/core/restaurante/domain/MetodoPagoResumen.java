package com.cafepos.core.restaurante.domain;

/**
 * Fila de GET /metodos-pago.
 *
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.caja
 * valide metodo_pago_id al cobrar y arme resumen_por_metodo_pago al
 * cerrar jornada — ver MetodoPagoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("metodoPagoResumen")
public record MetodoPagoResumen(Integer id, String nombre, String icono, boolean esEfectivo, String estado) {
}
