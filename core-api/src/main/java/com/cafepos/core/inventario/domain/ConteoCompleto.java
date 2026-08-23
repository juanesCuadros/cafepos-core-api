package com.cafepos.core.inventario.domain;

import java.time.OffsetDateTime;
import java.util.List;

/** Detalle completo de un conteo — misma forma para la respuesta del POST y de GET /conteos/{id}. */
public record ConteoCompleto(Integer id, OffsetDateTime fecha, String usuarioNombre,
                              List<ConteoDetalleItem> detalle) {
}
