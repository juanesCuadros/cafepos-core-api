package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * mesa_id NO lleva @NotNull aca a proposito: es obligatorio SOLO si
 * tipo='mesa' y debe venir ausente si tipo='venta_rapida' — esa
 * validacion cruzada de dos campos vive en PedidoService.abrir (ver
 * MesaIdObligatorioException / MesaIdNoPermitidoException), no en bean
 * validation.
 */
public record PedidoAbrirRequest(
        Integer mesaId,

        @NotBlank(message = "tipo es obligatorio")
        @Pattern(regexp = "mesa|venta_rapida", message = "tipo debe ser 'mesa' o 'venta_rapida'")
        String tipo) {
}
