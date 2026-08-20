package com.cafepos.admin.auth.infrastructure.web;

import com.cafepos.admin.auth.application.BootstrapSuperadminService;
import com.cafepos.admin.auth.application.LoginSuperadminService;
import com.cafepos.admin.auth.application.RefreshTokenService;
import com.cafepos.admin.auth.application.TokenPair;
import com.cafepos.admin.auth.domain.Superadmin;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
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
    public BootstrapResponse bootstrap(@Valid @RequestBody BootstrapRequest request) {
        Superadmin superadmin = bootstrapService.ejecutar(request.nombre(), request.correo(), request.password());
        return new BootstrapResponse(superadmin.getId(), superadmin.getNombre(), superadmin.getCorreo());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(loginService.ejecutar(request.correo(), request.password()));
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return toResponse(refreshTokenService.ejecutar(request.refreshToken()));
    }

    private TokenResponse toResponse(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds());
    }
}
