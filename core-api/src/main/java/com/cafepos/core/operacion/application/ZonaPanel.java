package com.cafepos.core.operacion.application;

import java.util.List;

/** Fila de GET /operacion/mesas — ver MesasPanelService. */
public record ZonaPanel(Integer id, String codigo, String icono, String nombre, List<MesaPanel> mesas) {
}
