package com.cafepos.core.inventario.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Insumo — implementado en infrastructure.persistence. */
public interface InsumoRepository {

    Insumo guardar(Insumo insumo);

    Optional<Insumo> buscarPorId(Integer id);

    List<InsumoResumen> listar(Integer categoriaInsumoId, String estado, String estadoStock, String q);

    /** join movimiento_inventario — usado por DELETE para decidir soft-delete vs borrado fisico (ver Producto). */
    boolean tieneMovimientosAsociados(Integer insumoId);

    void eliminar(Insumo insumo);
}
