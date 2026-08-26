package com.cafepos.core.gastos.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * comprobante_imagen: URL de texto plano, subida antes con POST /uploads —
 * este endpoint no procesa archivos (sin proveedor de storage real, mismo
 * criterio ya usado en Producto/Restaurante).
 */
public record GastoCrearRequest(
        @NotNull(message = "categoria_gasto_id es obligatorio") Integer categoriaGastoId,

        @NotBlank(message = "La descripcion es obligatoria") String descripcion,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a 0") BigDecimal monto,

        @NotBlank(message = "metodo_pago es obligatorio") String metodoPago,

        @NotNull(message = "La fecha es obligatoria") LocalDate fecha,

        String comprobanteImagen,

        String observaciones) {
}
