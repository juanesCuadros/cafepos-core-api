package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.EmpleadoResumen;

public record EmpleadoListItemResponse(Integer id, String codigo, String nombre, String cedulaEnmascarada,
                                        String cargo, String telefono, String estado) {

    public static EmpleadoListItemResponse de(EmpleadoResumen r) {
        return new EmpleadoListItemResponse(r.id(), r.codigo(), r.nombre(), r.cedulaEnmascarada(), r.cargo(),
                r.telefono(), r.estado());
    }
}
