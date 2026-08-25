package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.Proveedor;

/** GET /proveedores/{id} — detalle completo. */
public record ProveedorResponse(Integer id, String codigo, String nombre, String nit, String contacto,
                                 String telefono, String correo, String direccion, String estado) {

    public static ProveedorResponse de(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getNit(), p.getContacto(),
                p.getTelefono(), p.getCorreo(), p.getDireccion(), p.getEstado());
    }
}
