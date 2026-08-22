package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.DiasSemanaConverter;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.Promocion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

/** GET /promociones/{id} — todos los campos, dias_semana como arreglo (no el CSV interno de la columna). */
public record PromocionDetalleResponse(
        Integer id,
        String nombre,
        String tipoDescuento,
        BigDecimal valorDescuento,
        String aplicaA,
        List<ProductoRefResponse> productos,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFin,
        List<String> diasSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cantidadMinima,
        BigDecimal montoMinimo,
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
