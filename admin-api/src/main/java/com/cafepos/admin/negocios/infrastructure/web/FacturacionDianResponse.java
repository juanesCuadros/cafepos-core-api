package com.cafepos.admin.negocios.infrastructure.web;

/** NUNCA incluye client_id/client_secret/username/password, ni siquiera para confirmar que se guardaron bien. */
public record FacturacionDianResponse(Integer tenantId, boolean guardadoExitoso) {
}
