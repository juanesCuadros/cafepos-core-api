package com.cafepos.core.operacion.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de PedidoItem (y sus selecciones de combo) — implementado en infrastructure.persistence. */
public interface PedidoItemRepository {

    PedidoItem guardar(PedidoItem item);

    Optional<PedidoItem> buscarPorId(Integer id);

    List<PedidoItem> listarDePedido(Integer pedidoId);

    void eliminar(PedidoItem item);

    void guardarSeleccion(PedidoItemComboSeleccion seleccion);
}
