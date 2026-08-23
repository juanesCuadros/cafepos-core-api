package com.cafepos.core.operacion.application;

import java.util.List;

/** Resultado de PedidoService.enviarComanda — ver PedidoController. */
public record EnviarComandaResultado(String modo, List<Integer> itemsEnviados) {
}
