package com.cafepos.core.shared.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Publica en los canales WS que ya espera el frontend (ver WebSocketConfig).
 * A proposito el payload es minimo — solo "esto cambio", nunca el estado
 * completo: el cliente hace un refetch real por HTTP al recibir el mensaje,
 * en vez de confiar en que la forma del mensaje WS coincida con la del
 * recurso REST (mismo tipo de bug que ya aparecio hoy con
 * POST /pedidos/{id}/items devolviendo algo distinto de lo esperado — ver
 * hallazgo 2.9 en INTEGRACION.md). Menos superficie, menos riesgo.
 *
 * Conectado a los 4 hooks de negocio: ZonaService.cambiarEstadoMesa,
 * PedidoService.enviarComanda, KdsService.cambiarEstadoItem (kdsActualizado
 * + pedidoListo). facturaActualizada queda listo pero sin invocar todavia —
 * a la espera de la integracion con Factus (ver INTEGRACION.md 7.1).
 */
@Service
public class NotificacionesWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificacionesWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void mesaActualizada(Integer tenantId) {
        messagingTemplate.convertAndSend("/topic/mesas/" + tenantId, Map.of("evento", "mesa_actualizada"));
    }

    public void kdsActualizado(Integer tenantId) {
        messagingTemplate.convertAndSend("/topic/kds/" + tenantId, Map.of("evento", "kds_actualizado"));
    }

    public void pedidoListo(Integer tenantId, Integer pedidoId) {
        messagingTemplate.convertAndSend("/topic/pedido/" + tenantId + "/" + pedidoId,
                Map.of("evento", "pedido_listo"));
    }

    public void facturaActualizada(Integer tenantId, Integer facturaId) {
        messagingTemplate.convertAndSend("/topic/facturas/" + tenantId,
                Map.of("evento", "factura_actualizada", "factura_id", facturaId));
    }
}
