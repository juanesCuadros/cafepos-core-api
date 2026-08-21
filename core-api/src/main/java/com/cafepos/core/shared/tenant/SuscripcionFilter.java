package com.cafepos.core.shared.tenant;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
import com.cafepos.core.shared.seguridad.RutasAuth;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Aplica a TODAS las rutas autenticadas (no /auth/login ni /auth/refresh —
 * ese chequeo lo hace LoginService, ver su Javadoc; refresh no lo necesita,
 * un tenant suspendido igual puede/debe poder rotar hacia un login nuevo):
 * si tenants.estado es 'suspendido' o 'cancelado', rechaza con el mismo
 * mensaje distinto que usa el login — postura de lockout total, sin modo
 * degradado.
 *
 * Cache Caffeine corta (TTL 2 min) por tenant_id: no vale la pena pagar una
 * query a "tenants" en cada request de cada tenant activo solo para
 * detectar el caso raro de una suspension a mitad de sesion.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class SuscripcionFilter extends OncePerRequestFilter {

    private final TenantRepository tenantRepository;
    private final FilterErrorWriter filterErrorWriter;
    private final Cache<Integer, Tenant> estadoCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(2))
            .maximumSize(10_000)
            .build();

    public SuscripcionFilter(TenantRepository tenantRepository, FilterErrorWriter filterErrorWriter) {
        this.tenantRepository = tenantRepository;
        this.filterErrorWriter = filterErrorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        Integer tenantId = TenantContext.getCurrentTenantId();
        String path = request.getRequestURI();
        if (tenantId == null || RutasAuth.LOGIN.equals(path) || RutasAuth.REFRESH.equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Tenant tenant = estadoCache.get(tenantId, id -> tenantRepository.findById(id).orElse(null));
        if (tenant != null && tenant.estaSuspendidoOCancelado()) {
            filterErrorWriter.escribir(response, HttpStatus.FORBIDDEN.value(), tenant.mensajeBloqueo());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
