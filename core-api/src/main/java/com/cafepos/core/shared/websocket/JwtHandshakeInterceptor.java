package com.cafepos.core.shared.websocket;

import com.cafepos.core.shared.seguridad.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Exige un access token valido para conectar al WebSocket — sin esto,
 * cualquiera podria conectarse a /ws sin autenticarse. El navegador no puede
 * mandar el header Authorization en el handshake inicial de un WebSocket, por
 * eso el token viaja como query param (?token=...), unico lugar posible —
 * mismo access token que ya usa el resto de la API, sin mecanismo nuevo.
 *
 * tenantId/usuarioId quedan en los atributos de la sesion WS (accesibles
 * despues desde StompHeaderAccessor.getSessionAttributes()) — es lo que usa
 * TenantChannelInterceptor para autorizar cada SUBSCRIBE.
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Claims claims = jwtService.parseClaims(token);
            attributes.put("tenantId", jwtService.tenantId(claims));
            attributes.put("usuarioId", jwtService.usuarioId(claims));
            return true;
        } catch (RuntimeException ex) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                Exception exception) {
        // No hace falta nada aca — solo se usa beforeHandshake.
    }
}
