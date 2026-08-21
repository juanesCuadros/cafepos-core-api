package com.cafepos.admin.negocios.application;

public record NegocioCreado(Integer tenantId, String slug, String correoJefe, String passwordTemporal) {
}
