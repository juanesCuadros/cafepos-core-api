package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.ConteoResumen;

import java.time.OffsetDateTime;

public record ConteoListItemResponse(Integer id, OffsetDateTime fecha, String usuario, long numInsumos,
                                      long numDiferencias) {

    public static ConteoListItemResponse de(ConteoResumen resumen) {
        return new ConteoListItemResponse(resumen.id(), resumen.fecha(), resumen.usuarioNombre(),
                resumen.numInsumos(), resumen.numDiferencias());
    }
}
