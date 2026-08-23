package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.DiasSemanaConverter;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.Promocion;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * GET /promociones/{id} — todos los campos, dias_semana como arreglo (no
 * el CSV interno de la columna). valorDescuento SIEMPRE a 2 decimales
 * (@Monto) aunque tipo_descuento sea 'porcentaje' — la columna ya es
 * DECIMAL(12,2), consistente con el resto de campos de dinero.
 */
public record PromocionDetalleResponse(
        Integer id,
        String nombre,
        String tipoDescuento,
        @Monto BigDecimal valorDescuento,
        String aplicaA,
        List<ProductoRefResponse> productos,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFin,
        List<String> diasSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cantidadMinima,
        @Monto BigDecimal montoMinimo,
        String estado,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public static PromocionDetalleResponse de(Promocion p, List<ProductoRef> productos) {
        return new PromocionDetalleResponse(p.getId(), p.getNombre(), p.getTipoDescuento(), p.getValorDescuento(),
                p.getAplicaA(), productos.stream().map(ProductoRefResponse::de).toList(), p.getVigenciaInicio(),
                p.getVigenciaFin(), DiasSemanaConverter.aLista(p.getDiasSemana()), p.getHoraInicio(), p.getHoraFin(),
                p.getCantidadMinima(), p.getMontoMinimo(), p.getEstado(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
