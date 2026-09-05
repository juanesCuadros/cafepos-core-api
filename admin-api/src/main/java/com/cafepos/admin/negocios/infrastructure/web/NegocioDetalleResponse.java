package com.cafepos.admin.negocios.infrastructure.web;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record NegocioDetalleResponse(
        Integer id,
        String slug,
        String estado,
        OffsetDateTime fechaRegistro,
        LocalDate fechaProximaFacturacion,
        Integer superadminAprobadorId,
        RestauranteInfo restaurante,
        PlanInfo plan,
        FacturacionDianInfo facturacionDian,
        long totalUsuarios
) {
    public record RestauranteInfo(
            String nombreNegocio,
            String nit,
            String direccion,
            String departamento,
            String ciudad,
            String telefono,
            String correo
    ) {}

    public record PlanInfo(
            Integer id,
            String nombre,
            java.math.BigDecimal precioMensual,
            Integer limiteUsuarios,
            int diasPrueba
    ) {}

    public record FacturacionDianInfo(
            boolean configurada,
            String ambiente,
            String prefijo,
            String estado,
            LocalDate fechaVencimiento
    ) {}
}
