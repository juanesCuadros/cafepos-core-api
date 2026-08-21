package com.cafepos.admin.planes.infrastructure.web;

import com.cafepos.admin.planes.domain.Plan;

import java.math.BigDecimal;

public record PlanResponse(Integer id, String nombre, String descripcion, BigDecimal precioMensual,
                            Integer limiteUsuarios, int diasPrueba, String estado) {

    public static PlanResponse de(Plan plan) {
        return new PlanResponse(plan.getId(), plan.getNombre(), plan.getDescripcion(), plan.getPrecioMensual(),
                plan.getLimiteUsuarios(), plan.getDiasPrueba(), plan.getEstado());
    }
}
