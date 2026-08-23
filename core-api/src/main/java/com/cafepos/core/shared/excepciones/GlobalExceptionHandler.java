package com.cafepos.core.shared.excepciones;

import com.cafepos.core.shared.seguridad.CredencialesInvalidasException;
import com.cafepos.core.shared.seguridad.CuentaBloqueadaException;
import com.cafepos.core.shared.seguridad.PasswordActualIncorrectaException;
import com.cafepos.core.shared.seguridad.RefreshTokenInvalidoException;
import com.cafepos.core.shared.tenant.TenantNoEncontradoException;
import com.cafepos.core.shared.tenant.TenantSuspendidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(mensaje));
    }

    /**
     * NO se maneja aca a proposito — se relanza sin tocar para que Spring
     * Security (ExceptionTranslationFilter + AccessDeniedHandler) siga
     * siendo el unico que traduce una negacion real de permiso a 403. Sin
     * este metodo, el catch-all de Exception de mas abajo (mas generico)
     * la interceptaria primero y convertiria un 403 real en 500.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
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
