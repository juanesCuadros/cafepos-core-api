package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/** Un ajuste de salida o una perdida dejarian stock_actual negativo — ver AjusteService / PerdidaService. */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(BigDecimal disponible, BigDecimal solicitado) {
        super("Stock insuficiente: disponible " + disponible + ", solicitado " + solicitado);
    }
}
