package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = ApiTags.AUTENTICACION)
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final LogoutService logoutService;
    private final CambiarPasswordInicialService cambiarPasswordInicialService;

    public AuthController(LoginService loginService,
                           RefreshTokenService refreshTokenService,
                           LogoutService logoutService,
                           CambiarPasswordInicialService cambiarPasswordInicialService) {
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.logoutService = logoutService;
        this.cambiarPasswordInicialService = cambiarPasswordInicialService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuario",
            description = "El tenant ya se resolvio antes de este endpoint (subdominio, o header "
                    + "X-Tenant-Slug en dev) — no es un campo del body. Valida correo y contraseña, "
                    + "devuelve un access token JWT de corta duración y un refresh token opaco. "
                    + "\"debeCambiarPassword\" en la respuesta indica si el frontend debe redirigir a "
                    + "la pantalla de cambio de contraseña obligatorio antes que cualquier otra cosa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el par de tokens"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (correo mal formado o campos vacíos)"),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos, o usuario inactivo "
                    + "(mensaje genérico a propósito, no revela cuál de los tres fue)"),
            @ApiResponse(responseCode = "403", description = "El negocio está suspendido o cancelado — mensaje "
                    + "distinto al de credenciales, para que el frontend muestre la pantalla de bloqueo"),
            @ApiResponse(responseCode = "404", description = "El tenant resuelto (subdominio o X-Tenant-Slug) "
                    + "no corresponde a ningún negocio registrado")
    })
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return TokenResponse.de(loginService.ejecutar(request.correo(), request.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Rota el par de tokens",
            description = "Recibe el refresh token vigente, lo revoca y emite un par access+refresh "
                    + "nuevo. El refresh token recibido queda inválido apenas se usa, incluso si el "
                    + "par nuevo nunca llega a usarse. Tambien se rechaza (mismo error) si pasó más "
                    + "tiempo del permitido para el rol del usuario desde el último uso — hay que "
                    + "iniciar sesión de nuevo en vez de renovar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rotación correcta, devuelve el par de tokens nuevo"),
            @ApiResponse(responseCode = "400", description = "Falta el refresh token en el body"),
            @ApiResponse(responseCode = "401", description = "Refresh token inexistente, ya revocado, vencido, o "
                    + "sesión inactiva por más tiempo del permitido para el rol")
    })
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return TokenResponse.de(refreshTokenService.ejecutar(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cierra la sesion actual",
            description = "Revoca el refresh token recibido — a partir de este punto, ya no sirve para "
                    + "/auth/refresh. Idempotente a proposito: si el token no existe o ya estaba "
                    + "revocado, igual responde 200 (no hay razon para revelar el estado interno de un "
                    + "token que de cualquier forma ya no sirve).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesion cerrada (siempre, sin importar el estado previo del token)"),
            @ApiResponse(responseCode = "400", description = "Falta el refresh token en el body")
    })
    public LogoutResponse logout(@Valid @RequestBody RefreshRequest request) {
        logoutService.ejecutar(request.refreshToken());
        return LogoutResponse.SESION_CERRADA;
    }

    @PostMapping("/cambiar-password-inicial")
    @Operation(
            summary = "Cambia la contraseña temporal del primer login",
            description = "UNICO endpoint accesible mientras usuario.debe_cambiar_password sea true — "
                    + "todos los demás responden 403 hasta que este endpoint se complete con éxito "
                    + "(ver DebeCambiarPasswordFilter). Requiere el access token JWT del login previo. "
                    + "Valida passwordActual contra el hash guardado, exige passwordNueva de al menos "
                    + "12 caracteres, y devuelve un par de tokens nuevo (el token anterior sigue "
                    + "marcando debe_cambiar_password=true en su claim y quedaría bloqueado igual).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada, devuelve un par de tokens nuevo"),
            @ApiResponse(responseCode = "400", description = "passwordNueva con menos de 12 caracteres, o campos vacíos"),
            @ApiResponse(responseCode = "401", description = "Falta el access token, no es válido, o passwordActual "
                    + "no coincide con la contraseña actual")
    })
    public TokenResponse cambiarPasswordInicial(@Valid @RequestBody CambiarPasswordInicialRequest request,
                                                 Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return TokenResponse.de(cambiarPasswordInicialService.ejecutar(
                principal.usuarioId(), request.passwordActual(), request.passwordNueva()));
    }
}
