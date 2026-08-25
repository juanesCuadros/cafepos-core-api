package com.cafepos.core.shared.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Endpoint /ws (STOMP sobre SockJS) — canales /topic/mesas/{tenantId},
 * /topic/kds/{tenantId}, /topic/pedido/{tenantId}/{pedidoId},
 * /topic/facturas/{tenantId} (ver stomp-client.ts en el frontend, infraestructura
 * ya lista del lado cliente, nunca conectada hasta ahora).
 *
 * tenantId SIEMPRE en el path de cada canal a proposito (principio de este
 * SaaS: todo lleva tenant_id) — TenantChannelInterceptor rechaza cualquier
 * intento de suscripcion a un canal de un tenant distinto al de la sesion
 * autenticada, aunque el JWT ya haya resuelto el tenant en el handshake
 * (defensa en profundidad, no un solo punto de control).
 *
 * setAllowedOriginPatterns("*") a proposito: la seguridad real de este
 * endpoint es el JWT exigido en JwtHandshakeInterceptor, no el origen —
 * sin token valido no hay conexion, sin importar de donde venga.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final TenantChannelInterceptor tenantChannelInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor,
                            TenantChannelInterceptor tenantChannelInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
        this.tenantChannelInterceptor = tenantChannelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(tenantChannelInterceptor);
    }
}
