package com.cafepos.core.restaurante.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Mesa — implementado en infrastructure.persistence. */
public interface MesaRepository {

    Mesa guardar(Mesa mesa);

    Optional<Mesa> buscarPorId(Integer id);

    List<MesaResumen> listarDeZona(Integer zonaId);

    void eliminar(Mesa mesa);
}
