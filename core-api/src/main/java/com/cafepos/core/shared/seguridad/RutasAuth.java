package com.cafepos.core.shared.seguridad;

/**
 * Rutas de /auth/** compartidas entre SecurityConfig, SuscripcionFilter y
 * DebeCambiarPasswordFilter — centralizadas para no repetir el literal en
 * cada filtro.
 */
public final class RutasAuth {

    public static final String LOGIN = "/auth/login";
    public static final String REFRESH = "/auth/refresh";
    public static final String CAMBIAR_PASSWORD_INICIAL = "/auth/cambiar-password-inicial";

    private RutasAuth() {
    }
}
