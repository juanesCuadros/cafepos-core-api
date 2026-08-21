package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
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
 * Corre DESPUES de JwtAuthenticationFilter (ver SecurityConfig,
 * addFilterAfter) y ANTES de que el request llegue a cualquier controller.
 * Mientras usuario.debe_cambiar_password sea true (leido del claim del JWT,
 * sin ir a la base de datos), el UNICO endpoint alcanzable es
 * /auth/cambiar-password-inicial — todo lo demas responde 403.
 *
 * Si no hay autenticacion (request sin JWT valido, ej. /auth/login),
 * este filtro no hace nada — esa decision (401 vs permitAll) es de
 * authorizeHttpRequests, no de aca.
 */
public class DebeCambiarPasswordFilter extends OncePerRequestFilter {

    private final FilterErrorWriter filterErrorWriter;

    public DebeCambiarPasswordFilter(FilterErrorWriter filterErrorWriter) {
        this.filterErrorWriter = filterErrorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUsuario usuario
                && usuario.debeCambiarPassword()
                && !RutasAuth.CAMBIAR_PASSWORD_INICIAL.equals(request.getRequestURI())) {
            filterErrorWriter.escribir(response, HttpStatus.FORBIDDEN.value(),
                    "Debes cambiar tu contraseña temporal antes de continuar");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
