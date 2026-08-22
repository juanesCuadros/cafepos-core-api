package com.cafepos.core.productosmenu.domain;

/** Un elemento de dias_semana no es uno de los 7 dias validos (ver DiasSemanaConverter). */
public class DiaSemanaInvalidoException extends RuntimeException {

    public DiaSemanaInvalidoException(String valorInvalido) {
        super("dias_semana contiene un valor inválido: '" + valorInvalido + "' - debe ser uno de: "
                + String.join(", ", DiasSemanaConverter.DIAS_VALIDOS));
    }
}
