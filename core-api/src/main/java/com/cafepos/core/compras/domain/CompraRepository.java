package com.cafepos.core.compras.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Compra — implementado en infrastructure.persistence. */
public interface CompraRepository {

    Compra guardar(Compra compra);

    Optional<Compra> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo). */
    List<CompraListadoItem> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer proveedorId, String formaPago,
                                    String estado);
}
