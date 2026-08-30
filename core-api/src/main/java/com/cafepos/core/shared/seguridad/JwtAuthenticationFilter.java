package com.cafepos.core.shared.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lee "Authorization: Bearer <token>", valida el JWT (firma, issuer,
 * audience, expiracion via JwtService) y deja un AuthenticatedUsuario como
 * principal en el SecurityContext. Si no hay header, o el token no es
 * valido, sigue sin autenticar — el filtro NO rechaza el request, eso lo
 * decide authorizeHttpRequests() mas abajo en la chain (ver SecurityConfig).
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String token = extraerToken(request);
        if (token != null) {
            try {
                Claims claims = jwtService.parseClaims(token);
                AuthenticatedUsuario principal = new AuthenticatedUsuario(
                        jwtService.usuarioId(claims), jwtService.tenantId(claims),
                        jwtService.rolId(claims), jwtService.debeCambiarPassword(claims));
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Header Authorization normal para el resto de la API. Para /ws/**
     * ADEMAS acepta ?token=... por query param — el handshake de un
     * WebSocket y las peticiones HTTP auxiliares de SockJS (/ws/info, y los
     * transportes de respaldo tipo xhr-streaming) no pueden llevar el
     * header Authorization (el navegador no lo permite ahi), por eso
     * stomp-client.ts lo manda por query param. Sin esto, TenantFilter
     * nunca ve un JWT en estas rutas, cae a resolver tenant por subdominio
     * del Host, y como api.resttodash.app no es ningun tenant real,
     * rechaza con 404 "Negocio no encontrado" — confirmado en vivo
     * (25-ago-2026): /ws/websocket (el handshake final) ya funcionaba bien
     * porque lo cubre JwtHandshakeInterceptor aparte, pero /ws/info nunca
     * se probo hasta conectar desde un navegador real via SockJS.
     * Fuera de /ws/**, el query param se ignora a proposito — el header
     * sigue siendo la unica via para el resto de la API (un token en la
     * URL queda en logs de acceso, historial del navegador, etc., sin
     * necesidad real para esas rutas).
     */
    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        if (request.getRequestURI().startsWith(request.getContextPath() + "/ws/")) {
            String queryToken = request.getParameter("token");
            if (queryToken != null && !queryToken.isBlank()) {
                return queryToken;
            }
        }
        return null;
    }
}
