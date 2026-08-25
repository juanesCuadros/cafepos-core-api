package com.cafepos.core.shared.texto;

/**
 * Utilidad generica de enmascarado de documentos/identificadores
 * sensibles — vivio originalmente en clientes.domain, movida a shared
 * porque com.cafepos.core.personal (cedula_enmascarada) necesita el
 * mismo comportamiento exacto y no es logica de negocio propia de
 * Clientes. Regla ya establecida en cafepos_MASTER.md: el numero de
 * documento se muestra siempre enmascarado como "••••" (4 caracteres
 * fijos) seguido de los ultimos 4 digitos reales, sin importar la
 * longitud total.
 *
 * Caso raro (documento con MENOS de 4 caracteres): en vez de lanzar
 * excepcion o rellenar con datos falsos, se aplica la MISMA formula sin
 * caso especial - "ultimos 4" de un string mas corto que 4 es, por
 * definicion, el string entero. Resultado: para un documento de 2
 * digitos ("12"), el enmascarado queda "••••12" - no oculta nada (no hay
 * mas digitos para ocultar), pero mantiene el formato uniforme
 * "••••" + resto sin ambiguedad ni excepcion. Decision documentada aca
 * a proposito, ver conversacion de implementacion del modulo Clientes.
 */
public final class MascaraDocumento {

    private static final String PREFIJO = "••••";
    private static final int DIGITOS_VISIBLES = 4;

    private MascaraDocumento() {
    }

    public static String enmascarar(String numeroDocumento) {
        int desde = Math.max(0, numeroDocumento.length() - DIGITOS_VISIBLES);
        return PREFIJO + numeroDocumento.substring(desde);
    }
}
