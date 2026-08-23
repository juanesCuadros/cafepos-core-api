package com.cafepos.core.inventario.domain;

import java.time.OffsetDateTime;

/** Fila de GET /conteos — numDiferencias cuenta filas de detalle con diferencia != 0 (calculado en SQL). */
public record ConteoResumen(Integer id, OffsetDateTime fecha, String usuarioNombre, long numInsumos,
                             long numDiferencias) {
}
