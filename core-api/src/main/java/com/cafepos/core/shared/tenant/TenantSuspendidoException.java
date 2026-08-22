package com.cafepos.core.shared.tenant;

/**
 * Tenant en estado 'suspendido' o 'cancelado'. A proposito una excepcion
 * DISTINTA de las de credenciales/autorizacion (ver
 * shared.seguridad.CredencialesInvalidasException) — el frontend necesita
 * poder distinguir "credenciales malas" de "negocio bloqueado" para mostrar
 * la pantalla correcta.
 *
 * CODIGO tambien lo usa SuscripcionFilter (mismo caso de negocio, distinto
 * punto de deteccion: login vs. cualquier otro request de una sesion ya
 * activa) — mismo codigo para que el frontend lo trate igual sin importar
 * en que endpoint aparecio.
 */
public class TenantSuspendidoException extends RuntimeException {

    public static final String CODIGO = "NEGOCIO_SUSPENDIDO";

    public TenantSuspendidoException(String mensaje) {
        super(mensaje);
    }
}
