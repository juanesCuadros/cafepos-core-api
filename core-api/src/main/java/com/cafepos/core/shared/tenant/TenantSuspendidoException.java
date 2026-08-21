package com.cafepos.core.shared.tenant;

/**
 * Tenant en estado 'suspendido' o 'cancelado'. A proposito una excepcion
 * DISTINTA de las de credenciales/autorizacion (ver
 * shared.seguridad.CredencialesInvalidasException) — el frontend necesita
 * poder distinguir "credenciales malas" de "negocio bloqueado" para mostrar
 * la pantalla correcta.
 */
public class TenantSuspendidoException extends RuntimeException {

    public TenantSuspendidoException(String mensaje) {
        super(mensaje);
    }
}
