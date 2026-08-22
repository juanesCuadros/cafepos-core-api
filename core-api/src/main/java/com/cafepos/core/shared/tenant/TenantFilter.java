package com.cafepos.core.shared.tenant;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resuelve el tenant del request actual y lo deja disponible en
 * TenantContext (slug + id) mientras dura el request.
 *
 * Corre DESPUES de JwtAuthenticationFilter dentro de la security chain (ver
 * SecurityConfig, addFilterAfter) — eso es lo que permite la regla de abajo:
 * para cuando este filtro se ejecuta, ya se sabe si el request trae un JWT
 * valido o no.
 *
 * Dos fuentes de tenant, mutuamente excluyentes, nunca mezcladas:
 *
 *   1. Request CON JWT valido (JwtAuthenticationFilter ya dejo un
 *      AuthenticatedUsuario en el SecurityContext): el tenant_id sale
 *      EXCLUSIVAMENTE del claim tenant_id de ese JWT. El header
 *      X-Tenant-Slug NO se lee en absoluto en este caso — ni se compara, ni
 *      tiene ningun efecto. Un usuario autenticado de un tenant no puede
 *      pisar su propio tenant mandando el header de otro.
 *   2. Request SIN JWT (login, y cualquier otro endpoint publico que exista
 *      o llegue a existir): el tenant se resuelve por X-Tenant-Slug si
 *      viene presente, o por subdominio del Host si no. Esto aplica en
 *      cualquier ambiente, no solo dev — no hay JWT del cual sacar nada
 *      todavia, esta es la unica fuente posible aca.
 *
 * Si no se pudo resolver ningun slug en el caso 2 (ej. Swagger UI estatica,
 * actuator, host sin subdominio), el filtro simplemente no setea tenant_id
 * y sigue — eso no rompe nada que no requiera tenant. Si SI hay un slug
 * pero no corresponde a ningun tenant real, se rechaza aca mismo con 404:
 * dejar pasar el request con tenant_id=null haria que el primer "SET LOCAL"
 * (ver TenantAwareJpaTransactionManager) se saltee, y cualquier query a una
 * tabla con RLS activo fallaria con un error crudo de Postgres en vez de un
 * error claro.
 */
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";

    private final String baseDomain;
    private final TenantRepository tenantRepository;
    private final FilterErrorWriter filterErrorWriter;

    public TenantFilter(String baseDomain,
                         TenantRepository tenantRepository,
                         FilterErrorWriter filterErrorWriter) {
        this.baseDomain = baseDomain;
        this.tenantRepository = tenantRepository;
        this.filterErrorWriter = filterErrorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
            Integer tenantIdDelJwt = tenantIdDeRequestAutenticado();
            if (tenantIdDelJwt != null) {
                TenantContext.setCurrentTenantId(tenantIdDelJwt);
                filterChain.doFilter(request, response);
                return;
            }

            String slug = resolveSlug(request);
            if (slug == null) {
                filterChain.doFilter(request, response);
                return;
            }
            TenantContext.setCurrentSlug(slug);
            Tenant tenant = tenantRepository.findBySlug(slug).orElse(null);
            if (tenant == null) {
                filterErrorWriter.escribir(response, HttpStatus.NOT_FOUND.value(), "Negocio no encontrado");
                return;
            }
            TenantContext.setCurrentTenantId(tenant.getId());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Integer tenantIdDeRequestAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUsuario usuario) {
            return usuario.tenantId();
        }
        return null;
    }

    private String resolveSlug(HttpServletRequest request) {
        String header = request.getHeader(TENANT_SLUG_HEADER);
        if (header != null && !header.isBlank()) {
            return header.trim();
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
