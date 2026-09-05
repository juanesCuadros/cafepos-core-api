package com.cafepos.admin.shared;

import com.cafepos.admin.auth.domain.BootstrapNoDisponibleException;
import com.cafepos.admin.auth.domain.CredencialesInvalidasException;
import com.cafepos.admin.auth.domain.CuentaBloqueadaException;
import com.cafepos.admin.auth.domain.RefreshTokenInvalidoException;
import com.cafepos.admin.negocios.domain.OperacionTenantInvalidaException;
import com.cafepos.admin.negocios.domain.PlanNoExisteException;
import com.cafepos.admin.negocios.domain.SlugYaExisteException;
import com.cafepos.admin.negocios.domain.TenantNoEncontradoException;
import com.cafepos.admin.planes.domain.PlanNoEncontradoException;
import com.cafepos.admin.shared.criptografia.CifradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ErrorResponse(String mensaje) {
    }

    @ExceptionHandler(BootstrapNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleBootstrapNoDisponible(BootstrapNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CuentaBloqueadaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaBloqueada(CuentaBloqueadaException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenInvalido(RefreshTokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SlugYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleSlugYaExiste(SlugYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PlanNoExisteException.class)
    public ResponseEntity<ErrorResponse> handlePlanNoExiste(PlanNoExisteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PlanNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePlanNoEncontrado(PlanNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TenantNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTenantNoEncontrado(TenantNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(OperacionTenantInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleOperacionTenantInvalida(OperacionTenantInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Acceso denegado"));
    }

    @ExceptionHandler(CifradoException.class)
    public ResponseEntity<ErrorResponse> handleCifrado(CifradoException ex) {
        log.error("Error criptografico en operacion de credenciales", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno al procesar credenciales de seguridad"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleCatchAll(Exception ex) {
        log.error("Excepcion no controlada en admin-api", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Ha ocurrido un error inesperado en el servidor"));
    }
}
