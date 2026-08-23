package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.InsumoResumen;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record InsumoListItemResponse(Integer id, String codigo, String nombre, CategoriaInsumoRefResponse categoria,
                                      String unidadMedida, BigDecimal stockActual, BigDecimal stockMinimo,
                                      @Monto BigDecimal costoActual, @Monto BigDecimal valorTotal,
                                      String estadoStock, String estado, OffsetDateTime fechaRegistro) {

    public static InsumoListItemResponse de(InsumoResumen resumen) {
        return new InsumoListItemResponse(resumen.id(), resumen.codigo(), resumen.nombre(),
                new CategoriaInsumoRefResponse(resumen.categoriaInsumoId(), resumen.categoriaInsumoNombre()),
                resumen.unidadMedida(), resumen.stockActual(), resumen.stockMinimo(), resumen.costoActual(),
                resumen.valorTotal(), resumen.estadoStock(), resumen.estado(), resumen.fechaRegistro());
    }
}
