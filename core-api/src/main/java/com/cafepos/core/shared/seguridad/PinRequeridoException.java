package com.cafepos.core.shared.seguridad;

/**
 * Lanzada por PinStepUpService.validar cuando falla cualquiera de sus
 * chequeos (header ausente, JWT invalido/vencido, typ distinto de
 * "pin_stepup", tenant_id que no coincide con TenantContext, o
 * permiso_id/recurso_tipo/recurso_id que no coinciden EXACTO con lo
 * pedido) — un solo mensaje generico para los cinco casos, nunca revelar
 * cual fallo especificamente.
 */
public class PinRequeridoException extends RuntimeException {

    public static final String CODIGO = "PIN_REQUERIDO";

    public PinRequeridoException() {
        super("Se requiere autorización con PIN para esta acción");
    }
}
