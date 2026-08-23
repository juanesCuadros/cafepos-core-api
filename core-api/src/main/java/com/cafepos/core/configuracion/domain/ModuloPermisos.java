package com.cafepos.core.configuracion.domain;

import java.util.List;

public record ModuloPermisos(String moduloPadre, List<PermisoMatrizItem> permisos) {
}
