package com.cafepos.admin.negocios.infrastructure.web;

/** contrasenaTemporal viaja en texto plano a proposito: es la unica vez que se muestra. */
public record NegocioResponse(Integer tenantId, String slug, String urlCompleta,
                               String correoJefe, String contrasenaTemporal) {
}
