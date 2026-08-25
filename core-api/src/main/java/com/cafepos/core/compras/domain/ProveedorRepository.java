package com.cafepos.core.compras.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Proveedor — implementado en infrastructure.persistence. */
public interface ProveedorRepository {

    Proveedor guardar(Proveedor proveedor);

    Optional<Proveedor> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo). */
    List<ProveedorResumen> listar(String estado, String q);

    /** join compra — usado por DELETE para el 409 (ver ProveedorService.eliminar). */
    boolean tieneComprasAsociadas(Integer proveedorId);

    void eliminar(Proveedor proveedor);
}
