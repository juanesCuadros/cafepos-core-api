package com.cafepos.core.shared.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resuelve el slug del tenant para el request actual y lo deja disponible
 * en TenantContext mientras dura el request.
 *
 * Resolución:
 *   1. Perfil "dev" + header "X-Tenant-Slug" presente -> se usa el header
 *      (para poder probar desde Swagger UI en localhost, sin subdominio).
 *   2. Cualquier otro caso -> se parsea el subdominio del Host contra
 *      cafepos.tenant.base-domain (ej. "demo.cafepos.com" -> "demo").
 * El header se IGNORA por completo fuera de perfil "dev" — nunca es una
 * forma válida de resolver tenant fuera de desarrollo.
 *
 * LÍMITE IMPORTANTE — leer antes de asumir que esto ya aísla tenants:
 * este filtro SOLO resuelve el slug. NO consulta la base de datos para
 * convertirlo en tenant_id, y NO ejecuta "SET LOCAL app.current_tenant_id"
 * (el comando que activa las políticas de Row-Level Security de Postgres,
 * ver V1__schema_v4.sql). Eso requiere:
 *   - Una entidad/repositorio Tenant (no existen todavía en el proyecto —
 *     no hay ninguna entidad JPA en todo el código a la fecha).
 *   - Atar el SET LOCAL a la MISMA conexión/transacción que abre la capa
 *     de persistencia más adelante en el stack (open-in-view=false, así
 *     que la transacción no existe todavía en este filtro) — típicamente
 *     vía el SPI de multi-tenancy de Hibernate (CurrentTenantIdentifierResolver
 *     + MultiTenantConnectionProvider) o equivalente.
 * Hasta que esa pieza exista, RLS no tiene ninguna variable de sesión
 * seteada por la aplicación.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";
    private static final String DEV_PROFILE = "dev";

    private final Environment environment;
    private final String baseDomain;

    public TenantFilter(Environment environment,
                         @Value("${cafepos.tenant.base-domain}") String baseDomain) {
        this.environment = environment;
        this.baseDomain = baseDomain;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
            String slug = resolveSlug(request);
            if (slug != null) {
                TenantContext.setCurrentSlug(slug);
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveSlug(HttpServletRequest request) {
        if (environment.acceptsProfiles(Profiles.of(DEV_PROFILE))) {
            String header = request.getHeader(TENANT_SLUG_HEADER);
            if (header != null && !header.isBlank()) {
                return header.trim();
            }
        }
        return resolveSlugFromHost(request.getServerName());
    }

    private String resolveSlugFromHost(String host) {
        if (host == null) {
            return null;
        }
        String suffix = "." + baseDomain;
        if (host.endsWith(suffix) && host.length() > suffix.length()) {
            return host.substring(0, host.length() - suffix.length());
        }
        return null;
    }
}
