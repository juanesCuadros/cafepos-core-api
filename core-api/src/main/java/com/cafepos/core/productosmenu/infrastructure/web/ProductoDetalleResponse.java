package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.Producto;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** GET /productos/{id} — TODOS los campos de la tabla, sin resumir ni anidar (a diferencia del listado). */
public record ProductoDetalleResponse(
        Integer id,
        String codigo,
        String nombre,
        String descripcion,
        Integer categoriaId,
        Integer areaCocinaId,
        String imagen,
        @Monto BigDecimal precioVenta,
        @Monto BigDecimal costoEstimado,
        String tasaImpuesto,
        boolean manejaReceta,
        boolean manejaInventario,
        String unidadMedida,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        String estado,
        String visibilidad,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public static ProductoDetalleResponse de(Producto p) {
        return new ProductoDetalleResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
                p.getCategoriaId(), p.getAreaCocinaId(), p.getImagen(), p.getPrecioVenta(), p.getCostoEstimado(),
                p.getTasaImpuesto(), p.isManejaReceta(), p.isManejaInventario(), p.getUnidadMedida(),
                p.getStockActual(), p.getStockMinimo(), p.getEstado(), p.getVisibilidad(), p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
