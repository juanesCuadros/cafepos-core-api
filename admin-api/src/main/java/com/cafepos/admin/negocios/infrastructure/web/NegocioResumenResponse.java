package com.cafepos.admin.negocios.infrastructure.web;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record NegocioResumenResponse(
        Integer id,
        String slug,
        String nombreNegocio,
        Integer planId,
        String planNombre,
        String estado,
        OffsetDateTime fechaRegistro,
        LocalDate fechaProximaFacturacion
) {
}
