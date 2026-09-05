package com.cafepos.admin.auth.infrastructure.web;

import com.cafepos.admin.auth.application.BootstrapSuperadminService;
import com.cafepos.admin.auth.application.GestionSuperadminService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@Tag(name = "Autenticacion")
public class AuthController {

    private final BootstrapSuperadminService bootstrapService;
    private final LoginSuperadminService loginService;
    private final RefreshTokenService refreshTokenService;
    private final GestionSuperadminService gestionSuperadminService;

    public AuthController(BootstrapSuperadminService bootstrapService,
                           LoginSuperadminService loginService,
                           RefreshTokenService refreshTokenService,
                           GestionSuperadminService gestionSuperadminService) {
        this.bootstrapService = bootstrapService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.gestionSuperadminService = gestionSuperadminService;
    }

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crea el primer y único Super Admin",
            description = "Endpoint de un solo uso, para siempre: solo funciona si la tabla de "
                    + "Super Admins está vacía. Apenas existe un Super Admin, este endpoint queda "
                    + "autobloqueado de forma permanente y responde 403 sin excepción.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Super Admin creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Ya existe un Super Admin")
    })
    public BootstrapResponse bootstrap(@Valid @RequestBody BootstrapRequest request) {
        Superadmin superadmin = bootstrapService.ejecutar(request.nombre(), request.correo(), request.password());
        return new BootstrapResponse(superadmin.getId(), superadmin.getNombre(), superadmin.getCorreo());
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de Super Admin",
            description = "Valida correo y contraseña. Bloquea la cuenta temporalmente por 30 minutos al 5to intento fallido consecutivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el par de tokens"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos, o cuenta inactiva"),
            @ApiResponse(responseCode = "423", description = "Cuenta bloqueada temporalmente por fuerza bruta")
    })
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(loginService.ejecutar(request.correo(), request.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Rota el par de tokens",
            description = "Recibe el refresh token vigente, lo revoca y emite un par access+refresh nuevo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rotación correcta"),
            @ApiResponse(responseCode = "400", description = "Falta el refresh token"),
            @ApiResponse(responseCode = "401", description = "Refresh token inexistente, ya revocado o vencido")
    })
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return toResponse(refreshTokenService.ejecutar(request.refreshToken()));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Perfil del Super Admin autenticado",
            description = "Devuelve los datos del Super Admin actual basado en el JWT de sesión.")
    public SuperadminPerfilResponse me(Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        Superadmin superadmin = gestionSuperadminService.obtenerPerfil(superadminId);
        return SuperadminPerfilResponse.de(superadmin);
    }

    @PutMapping("/password")
    @Operation(
            summary = "Cambio de contraseña del Super Admin",
            description = "Permite cambiar la propia contraseña validando la contraseña actual.")
    public Map<String, String> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request,
                                               Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionSuperadminService.cambiarPassword(superadminId, request.passwordActual(), request.passwordNuevo());
        return Map.of("mensaje", "Contraseña actualizada exitosamente");
    }

    private TokenResponse toResponse(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds());
    }
}
