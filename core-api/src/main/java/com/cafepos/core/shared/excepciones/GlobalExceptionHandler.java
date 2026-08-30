package com.cafepos.core.shared.excepciones;

import com.cafepos.core.shared.seguridad.CredencialesInvalidasException;
import com.cafepos.core.shared.seguridad.CuentaBloqueadaException;
import com.cafepos.core.shared.seguridad.PasswordActualIncorrectaException;
import com.cafepos.core.shared.seguridad.PermisoNoEncontradoException;
import com.cafepos.core.shared.seguridad.PinBloqueadoException;
import com.cafepos.core.shared.seguridad.PinIncorrectoException;
import com.cafepos.core.shared.seguridad.PinIncorrectoResponse;
import com.cafepos.core.shared.seguridad.PinRequeridoException;
import com.cafepos.core.shared.seguridad.RefreshTokenInvalidoException;
import com.cafepos.core.shared.tenant.TenantNoEncontradoException;
import com.cafepos.core.shared.tenant.TenantSuspendidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Ademas de los handlers especificos de abajo, esta clase es la UNICA con
 * un @ExceptionHandler(Exception.class) catch-all de todo el proyecto (ver
 * el de mas abajo) — a proposito global (sin basePackageClasses), para que
 * cubra cualquier controller de cualquier modulo.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorConCodigoResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorConCodigoResponse(ex.getMessage(), CredencialesInvalidasException.CODIGO));
    }

    @ExceptionHandler(PasswordActualIncorrectaException.class)
    public ResponseEntity<ErrorResponse> handlePasswordActualIncorrecta(PasswordActualIncorrectaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CuentaBloqueadaException.class)
    public ResponseEntity<ErrorConCodigoResponse> handleCuentaBloqueada(CuentaBloqueadaException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(new ErrorConCodigoResponse(ex.getMessage(), CuentaBloqueadaException.CODIGO));
    }

    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenInvalido(RefreshTokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TenantSuspendidoException.class)
    public ResponseEntity<ErrorConCodigoResponse> handleTenantSuspendido(TenantSuspendidoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorConCodigoResponse(ex.getMessage(), TenantSuspendidoException.CODIGO));
    }

    @ExceptionHandler(TenantNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTenantNoEncontrado(TenantNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PinIncorrectoException.class)
    public ResponseEntity<PinIncorrectoResponse> handlePinIncorrecto(PinIncorrectoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new PinIncorrectoResponse(false, ex.getMessage(), PinIncorrectoException.CODIGO,
                        ex.getIntentosRestantes()));
    }

    @ExceptionHandler(PinBloqueadoException.class)
    public ResponseEntity<ErrorConCodigoResponse> handlePinBloqueado(PinBloqueadoException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(new ErrorConCodigoResponse(ex.getMessage(), PinBloqueadoException.CODIGO));
    }

    @ExceptionHandler(PinRequeridoException.class)
    public ResponseEntity<ErrorConCodigoResponse> handlePinRequerido(PinRequeridoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorConCodigoResponse(ex.getMessage(), PinRequeridoException.CODIGO));
    }

    @ExceptionHandler(PermisoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePermisoNoEncontrado(PermisoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Ruta que no matchea ningun controller (URL mal escrita) — sin esto,
     * Spring la trata como intento de servir un recurso estatico, falla, y
     * caia en el catch-all de mas abajo como 500 generico, indistinguible de
     * un bug real. Devuelve 404 real.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaNoEncontrada(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Ruta no encontrada"));
    }

    /** Verbo HTTP no soportado en una ruta que si existe (ej. DELETE en una ruta que solo tiene GET/POST). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(mensaje));
    }

    /**
     * Devuelve 403 DIRECTO aca, ya NO relanza la excepcion — confirmado
     * real (ver reporte de implementacion del modulo Gastos, RBAC test de
     * Cajero/Admin) que relanzarla NO llega a ser resuelta por Spring
     * Security's ExceptionTranslationFilter dentro del MISMO dispatch: un
     * AccessDeniedException lanzado por @PreAuthorize desde DENTRO del
     * metodo del controller escapa de ExceptionHandlerExceptionResolver
     * (este handler la relanzaba, ningun resolver la marcaba "resuelta"),
     * Spring Boot la reenvia internamente a "/error" via un dispatch de
     * tipo ERROR, y en ESE dispatch los filtros de Spring Security (que
     * fijaron el Authentication real en el dispatch original) no vuelven a
     * correr — termina viendo un SecurityContext vacio/anonimo,
     * ExceptionTranslationFilter lo clasifica como "necesita
     * autenticacion" y devuelve el 401 "NO_AUTENTICADO" en vez de un 403
     * real. Bug pre-existente de TODO el proyecto (afecta cualquier
     * @PreAuthorize denegado, no solo Gastos), no algo que dependa del
     * catalogo de permisos de un modulo puntual. Resolver la excepcion
     * ACA, dentro del ciclo normal de DispatcherServlet, evita el reenvio
     * a /error por completo — mismo principio que el catch-all de mas
     * abajo, ya documentado para el caso generico.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "No tienes permiso para realizar esta accion";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(mensaje));
    }

    /**
     * Catch-all: cualquier excepcion no manejada por un handler especifico
     * (de aca o de un modulo) es un bug real, nunca una negacion de
     * permiso — antes de esto, una excepcion asi escapaba sin resolver,
     * Spring Boot reenviaba internamente a "/error", y como esa ruta cae
     * bajo el anyRequest().authenticated() de SecurityConfig sin el
     * Authentication original (los filtros propios no vuelven a correr en
     * un dispatch de tipo ERROR), terminaba en un 403 vacio sin relacion
     * ninguna con permisos — indistinguible de una negacion real. Este
     * catch-all resuelve la excepcion ADENTRO del ciclo normal de
     * DispatcherServlet, antes de que llegue a necesitar ese reenvio.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExcepcionNoManejada(Exception ex) {
        log.error("Excepción no manejada", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Ocurrió un error inesperado, contacta a soporte si persiste"));
    }
}
