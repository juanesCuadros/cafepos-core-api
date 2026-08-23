package com.cafepos.core.configuracion.domain;

import java.util.List;

public record MatrizRolPermisos(String rol, boolean esEditable, List<ModuloPermisos> modulos) {
}
