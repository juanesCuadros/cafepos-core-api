package com.cafepos.core.restaurante.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Zona — implementado en infrastructure.persistence. */
public interface ZonaRepository {

    Zona guardar(Zona zona);

    Optional<Zona> buscarPorId(Integer id);

    List<ZonaResumen> listarConNumMesas();

    long contarMesas(Integer zonaId);

    void eliminar(Zona zona);
}
