package com.cafepos.admin.negocios.infrastructure.web;

import com.cafepos.admin.negocios.application.VencimientoPruebaResultado;

import java.util.List;

public record VencimientoPruebaResponse(int candidatos, int suspendidos, List<String> slugsSuspendidos) {

    public static VencimientoPruebaResponse de(VencimientoPruebaResultado resultado) {
        return new VencimientoPruebaResponse(resultado.candidatos(), resultado.suspendidos(), resultado.slugsSuspendidos());
    }
}
