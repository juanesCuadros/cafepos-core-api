package com.cafepos.core.compras.domain;

/** DELETE /proveedores/{id} — 409 si tiene compras asociadas (COUNT real, ver ProveedorRepository.tieneComprasAsociadas). */
public class ProveedorConComprasException extends RuntimeException {

    public ProveedorConComprasException() {
        super("No se puede eliminar el proveedor porque tiene compras asociadas");
    }
}
