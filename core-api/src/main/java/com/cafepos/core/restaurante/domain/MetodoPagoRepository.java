package com.cafepos.core.restaurante.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de MetodoPago — implementado en infrastructure.persistence. */
public interface MetodoPagoRepository {

    MetodoPago guardar(MetodoPago metodoPago);

    Optional<MetodoPago> buscarPorId(Integer id);

    List<MetodoPagoResumen> listar();

    void eliminar(MetodoPago metodoPago);
}
