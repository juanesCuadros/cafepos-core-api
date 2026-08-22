package com.cafepos.core.shared.codigo;

/**
 * Codigo legible generado despues del INSERT (el id autoincremental ya
 * existe), formato "{PREFIJO}-{id con padding a 4 digitos}" - ej PROD-0015.
 * Mismo patron usado por producto, y mas adelante por compras, ventas y
 * gastos (todos tienen columna codigo).
 */
public final class GeneradorCodigo {

    private GeneradorCodigo() {
    }

    public static String generar(String prefijo, Integer id) {
        return prefijo + "-" + String.format("%04d", id);
    }
}
