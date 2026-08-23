package com.cafepos.core.operacion.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Pedido — implementado en infrastructure.persistence. */
public interface PedidoRepository {

    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Integer id);

    /** Pedido con estado != 'cerrado' de esa mesa, si existe — ver PedidoService.abrir. */
    Optional<Pedido> buscarActivoPorMesa(Integer mesaId);

    /**
     * Pedidos con comanda enviada (estado IN enviado/listo), ordenados por id
     * ascendente — proxy de "orden de llegada" mientras no exista una
     * columna real de fecha_envio (ver PedidoJpaRepository). Ver KdsService.listar.
     */
    List<Pedido> listarEnviadosOListos();
}
