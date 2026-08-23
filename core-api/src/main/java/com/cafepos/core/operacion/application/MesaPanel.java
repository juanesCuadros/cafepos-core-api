package com.cafepos.core.operacion.application;

import java.time.OffsetDateTime;

/** Fila de GET /operacion/mesas — ver MesasPanelService. */
public record MesaPanel(Integer id, String codigo, String numero, int capacidad, String estado, Integer pedidoId,
                         OffsetDateTime ocupadaDesde) {
}
