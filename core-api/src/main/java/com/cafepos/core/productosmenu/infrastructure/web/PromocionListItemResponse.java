package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.PromocionResumen;

import java.time.LocalDate;

public record PromocionListItemResponse(Integer id, String nombre, String tipoDescuento, LocalDate vigenciaInicio,
                                         LocalDate vigenciaFin, String estado) {

    public static PromocionListItemResponse de(PromocionResumen resumen) {
        return new PromocionListItemResponse(resumen.id(), resumen.nombre(), resumen.tipoDescuento(),
                resumen.vigenciaInicio(), resumen.vigenciaFin(), resumen.estado());
    }
}
