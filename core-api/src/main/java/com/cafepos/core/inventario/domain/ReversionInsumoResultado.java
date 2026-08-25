package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/**
 * Resultado de revertir stock/costo de un insumo al anular una compra
 * (com.cafepos.core.compras) — exitoso=false (nada se muto) si la
 * cantidad a revertir dejaria stock_actual negativo, para que el caller
 * decida el 400 con su propio tipo de excepcion (nunca propagar
 * StockInsuficienteException de este modulo a traves del limite).
 *
 * @NamedInterface propio, ver InsumoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("reversionInsumoResultado")
public record ReversionInsumoResultado(boolean exitoso, BigDecimal stockDisponible) {
}
