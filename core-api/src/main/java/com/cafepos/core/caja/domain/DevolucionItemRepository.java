package com.cafepos.core.caja.domain;

import java.util.List;

/** Puerto de persistencia de DevolucionItem — implementado en infrastructure.persistence. */
public interface DevolucionItemRepository {

    DevolucionItem guardar(DevolucionItem item);

    List<DevolucionItem> listarDeDevolucion(Integer devolucionId);
}
