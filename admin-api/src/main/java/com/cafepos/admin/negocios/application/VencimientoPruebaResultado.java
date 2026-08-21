package com.cafepos.admin.negocios.application;

import java.util.List;

public record VencimientoPruebaResultado(int candidatos, int suspendidos, List<String> slugsSuspendidos) {
}
