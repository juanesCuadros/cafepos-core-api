package com.cafepos.core.configuracion.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @JsonProperty explicito a proposito, unica excepcion a la regla de
 * snake_case automatico: la estrategia SNAKE_CASE de Jackson colapsa
 * mayusculas consecutivas ("aplicadoAUsuarios" -> "aplicado_ausuarios",
 * no "aplicado_a_usuarios") porque solo inserta guion bajo en una
 * transicion minuscula->mayuscula, nunca entre dos mayusculas seguidas.
 */
public record PermisosActualizadosResponse(String mensaje,
                                            @JsonProperty("aplicado_a_usuarios") int aplicadoAUsuarios) {

    public static PermisosActualizadosResponse de(int aplicadoAUsuarios) {
        return new PermisosActualizadosResponse("Permisos actualizados", aplicadoAUsuarios);
    }
}
