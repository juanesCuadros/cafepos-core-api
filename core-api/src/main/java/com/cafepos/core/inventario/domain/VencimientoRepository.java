package com.cafepos.core.inventario.domain;

import java.util.List;

/** Puerto de solo lectura para el lote mas proximo a vencer por insumo — implementado en infrastructure.persistence. */
public interface VencimientoRepository {

    List<LoteVencimiento> listar(String estado, Integer categoriaInsumoId);
}
