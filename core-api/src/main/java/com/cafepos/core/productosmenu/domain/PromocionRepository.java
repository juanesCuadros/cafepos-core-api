package com.cafepos.core.productosmenu.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Promocion — implementado en infrastructure.persistence. */
public interface PromocionRepository {

    Promocion guardar(Promocion promocion);

    Optional<Promocion> buscarPorId(Integer id);

    List<PromocionResumen> listar();

    void eliminar(Promocion promocion);

    /** Borra las asociaciones actuales y guarda las nuevas — reemplazo completo, no merge. */
    void reemplazarProductos(Integer promocionId, Integer tenantId, List<Integer> productoIds);

    List<ProductoRef> productosDe(Integer promocionId);

    /** Entidades completas (no PromocionResumen) — usado para evaluar aplicabilidad, ver PromocionService.evaluarSugeridas. */
    List<Promocion> listarActivas();
}
