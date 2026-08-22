package com.cafepos.core.productosmenu.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * dias_semana viaja como arreglo en la API pero se guarda como CSV en la
 * columna promocion.dias_semana (VARCHAR(100), ver V14). null o arreglo
 * vacio significa "sin restriccion de dia" - se guarda como NULL, nunca
 * como string vacio.
 */
public final class DiasSemanaConverter {

    public static final Set<String> DIAS_VALIDOS = new LinkedHashSet<>(List.of(
            "lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"));

    private DiasSemanaConverter() {
    }

    public static String aCsv(List<String> dias) {
        if (dias == null || dias.isEmpty()) {
            return null;
        }
        for (String dia : dias) {
            if (!DIAS_VALIDOS.contains(dia)) {
                throw new DiaSemanaInvalidoException(dia);
            }
        }
        return String.join(",", dias);
    }

    public static List<String> aLista(String csv) {
        if (csv == null || csv.isBlank()) {
            return null;
        }
        return List.of(csv.split(","));
    }
}
