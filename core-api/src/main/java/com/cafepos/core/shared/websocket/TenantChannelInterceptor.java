package com.cafepos.core.shared.websocket;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Autoriza cada SUBSCRIBE: el tenantId en el path del canal
 * (/topic/{canal}/{tenantId}[/...]) tiene que coincidir con el tenantId de
 * la sesion autenticada en el handshake (ver JwtHandshakeInterceptor) — sin
 * esto, con solo saber la forma de la URL cualquier tenant podria escuchar
 * los canales de otro. Formato de los 4 canales elegido a proposito para que
 * el tenantId quede siempre en la misma posicion (segments[3]):
 * /topic/mesas/{tenantId}, /topic/kds/{tenantId},
 * /topic/pedido/{tenantId}/{pedidoId}, /topic/facturas/{tenantId}.
 */
@Component
public class TenantChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return message;
        }

        String destino = accessor.getDestination();
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Integer tenantIdSesion = sessionAttributes == null ? null : (Integer) sessionAttributes.get("tenantId");

        if (destino == null || tenantIdSesion == null || !tenantIdCoincide(destino, tenantIdSesion)) {
            throw new AccessDeniedException("No autorizado para este canal");
        }
        return message;
    }

    private boolean tenantIdCoincide(String destino, Integer tenantIdSesion) {
        String[] segmentos = destino.split("/");
        // "" / "topic" / canal / tenantId [/ ...] → tenantId siempre en indice 3.
        if (segmentos.length < 4) {
            return false;
        }
        return String.valueOf(tenantIdSesion).equals(segmentos[3]);
    }
}
