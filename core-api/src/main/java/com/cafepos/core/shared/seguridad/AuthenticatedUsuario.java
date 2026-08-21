package com.cafepos.core.shared.seguridad;

/**
 * Principal puesto en el SecurityContext por JwtAuthenticationFilter — los
 * datos del JWT ya parseados, sin volver a tocar la base de datos en cada
 * request. DebeCambiarPasswordFilter lee debeCambiarPassword de aca.
 */
public record AuthenticatedUsuario(Integer usuarioId, Integer tenantId, Integer rolId, boolean debeCambiarPassword) {
}
