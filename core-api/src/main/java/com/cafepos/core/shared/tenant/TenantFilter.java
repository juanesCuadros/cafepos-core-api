package com.cafepos.core.shared.tenant;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resuelve el tenant del request actual y lo deja disponible en
 * TenantContext (slug + id) mientras dura el request.
 *
 * Resolución del SLUG:
 *   1. Perfil "dev" + header "X-Tenant-Slug" presente -> se usa el header
 *      (para poder probar desde Swagger UI en localhost, sin subdominio).
 *   2. Cualquier otro caso -> se parsea el subdominio del Host contra
 *      cafepos.tenant.base-domain (ej. "demo.cafepos.com" -> "demo").
 * El header se IGNORA por completo fuera de perfil "dev".
 *
 * Si no se pudo resolver ningún slug (ej. Swagger UI estática, actuator,
 * host sin subdominio), el filtro simplemente no setea tenant_id y sigue —
 * eso no rompe nada que no requiera tenant. Si SÍ hay un slug pero no
 * corresponde a ningún tenant real, se rechaza aca mismo con 404: dejar
 * pasar el request con tenant_id=null haría que el primer "SET LOCAL"
 * (ver TenantAwareJpaTransactionManager) se saltee, y cualquier query a una
 * tabla con RLS activo fallaría con un error crudo de Postgres en vez de un
 * error claro.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";
    private static final String DEV_PROFILE = "dev";

    private final Environment environment;
    private final String baseDomain;
    private final TenantRepository tenantRepository;
    private final FilterErrorWriter filterErrorWriter;

    public TenantFilter(Environment environment,
                         @Value("${cafepos.tenant.base-domain}") String baseDomain,
                         TenantRepository tenantRepository,
                         FilterErrorWriter filterErrorWriter) {
        this.environment = environment;
        this.baseDomain = baseDomain;
        this.tenantRepository = tenantRepository;
        this.filterErrorWriter = filterErrorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
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
