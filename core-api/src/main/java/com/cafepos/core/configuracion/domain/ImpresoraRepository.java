package com.cafepos.core.configuracion.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Impresora — implementado en infrastructure.persistence. */
public interface ImpresoraRepository {

    Impresora guardar(Impresora impresora);

    Optional<Impresora> buscarPorId(Integer id);

    List<Impresora> listar();

    void eliminar(Impresora impresora);
}
