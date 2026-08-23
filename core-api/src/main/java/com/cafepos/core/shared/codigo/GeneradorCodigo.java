package com.cafepos.core.shared.codigo;

/**
 * Codigo legible generado despues del INSERT (el id autoincremental ya
 * existe), formato "{PREFIJO}-{id con padding a N digitos}" - ej PROD-0015
 * (padding 4) o Z-001 (padding 3, ver Zona/Mesa en el modulo Restaurante).
 * Mismo patron usado por producto, combo, y mas adelante por compras,
 * ventas y gastos (todos tienen columna codigo).
 */
public final class GeneradorCodigo {

    private static final int PADDING_DEFAULT = 4;

    private GeneradorCodigo() {
    }

    public static String generar(String prefijo, Integer id) {
        return generar(prefijo, id, PADDING_DEFAULT);
    }

    public static String generar(String prefijo, Integer id, int padding) {
        return prefijo + "-" + String.format("%0" + padding + "d", id);
    }
}
