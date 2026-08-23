package com.cafepos.core.configuracion.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de AreaCocina — implementado en infrastructure.persistence. */
public interface AreaCocinaRepository {

    AreaCocina guardar(AreaCocina areaCocina);

    Optional<AreaCocina> buscarPorId(Integer id);

    List<AreaCocina> listar();

    void eliminar(AreaCocina areaCocina);
}
