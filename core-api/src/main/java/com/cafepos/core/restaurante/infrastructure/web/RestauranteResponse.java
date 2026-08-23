package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.RedesSociales;
import com.cafepos.core.restaurante.domain.Restaurante;

/** GET/PATCH /restaurante — misma forma en ambos casos, ver contrato api_10_restaurante.md 10.1. */
public record RestauranteResponse(String nombreNegocio, String nit, String logoUrl, String direccion,
                                   String departamento, String ciudad, String pais, String telefono, String correo,
                                   String telefonoRepresentante, RedesSociales redesSociales, String descripcion) {

    public static RestauranteResponse de(Restaurante r) {
        return new RestauranteResponse(r.getNombreNegocio(), r.getNit(), r.getLogoUrl(), r.getDireccion(),
                r.getDepartamento(), r.getCiudad(), r.getPais(), r.getTelefono(), r.getCorreo(),
                r.getTelefonoRepresentante(), r.getRedesSociales(), r.getDescripcion());
    }
}
