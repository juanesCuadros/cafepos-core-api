package com.cafepos.admin.auth.infrastructure.web;

import com.cafepos.admin.auth.application.BootstrapSuperadminService;
import com.cafepos.admin.auth.application.LoginSuperadminService;
import com.cafepos.admin.auth.application.RefreshTokenService;
import com.cafepos.admin.auth.application.TokenPair;
import com.cafepos.admin.auth.domain.Superadmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
@Tag(name = "Autenticacion")
public class AuthController {

    private final BootstrapSuperadminService bootstrapService;
    private final LoginSuperadminService loginService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(BootstrapSuperadminService bootstrapService,
                           LoginSuperadminService loginService,
                           RefreshTokenService refreshTokenService) {
        this.bootstrapService = bootstrapService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crea el primer y único Super Admin",
            description = "Endpoint de un solo uso, para siempre: solo funciona si la tabla de "
                    + "Super Admins está vacía. Apenas existe un Super Admin, este endpoint queda "
                    + "autobloqueado de forma permanente y responde 403 sin excepción, sin importar "
                    + "los datos que se envíen — no hay forma de volver a habilitarlo desde la API.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Super Admin creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (correo mal formado o contraseña de menos de 12 caracteres)"),
            @ApiResponse(responseCode = "403", description = "Ya existe un Super Admin — el bootstrap ya fue usado")
    })
    public BootstrapResponse bootstrap(@Valid @RequestBody BootstrapRequest request) {
        Superadmin superadmin = bootstrapService.ejecutar(request.nombre(), request.correo(), request.password());
        return new BootstrapResponse(superadmin.getId(), superadmin.getNombre(), superadmin.getCorreo());
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de Super Admin",
            description = "Valida correo y contraseña contra el registro creado en el bootstrap. "
                    + "Devuelve un access token JWT de corta duración y un refresh token opaco.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el par de tokens"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (correo mal formado o campos vacíos)"),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos, o cuenta inactiva "
                    + "(mensaje genérico a propósito, no revela cuál de los tres fue)")
    })
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(loginService.ejecutar(request.correo(), request.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Rota el par de tokens",
            description = "Recibe el refresh token vigente, lo revoca y emite un par access+refresh "
                    + "nuevo. El refresh token recibido queda inválido apenas se usa, incluso si el "
                    + "par nuevo nunca llega a usarse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rotación correcta, devuelve el par de tokens nuevo"),
            @ApiResponse(responseCode = "400", description = "Falta el refresh token en el body"),
            @ApiResponse(responseCode = "401", description = "Refresh token inexistente, ya revocado o vencido")
    })
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return toResponse(refreshTokenService.ejecutar(request.refreshToken()));
    }

    private TokenResponse toResponse(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds());
    }
}
