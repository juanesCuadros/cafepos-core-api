package com.cafepos.admin.auth;

import com.cafepos.admin.auth.application.LoginSuperadminService;
import com.cafepos.admin.auth.application.TokenPair;
import com.cafepos.admin.auth.application.TokenPairIssuer;
import com.cafepos.admin.auth.domain.CredencialesInvalidasException;
import com.cafepos.admin.auth.domain.CuentaBloqueadaException;
import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginSuperadminServiceTest {

    @Mock
    private SuperadminRepository superadminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenPairIssuer tokenPairIssuer;

    @InjectMocks
    private LoginSuperadminService service;

    @Test
    void ejecutar_credencialesCorrectas_emiteTokenPairYReseteaIntentos() {
        Superadmin superadmin = new Superadmin("Admin", "admin@cafepos.com", "$2a$hash");
        when(superadminRepository.findByCorreo("admin@cafepos.com")).thenReturn(Optional.of(superadmin));
        when(passwordEncoder.matches("ClaveSegura2026", "$2a$hash")).thenReturn(true);
        when(tokenPairIssuer.emitir(superadmin.getId())).thenReturn(new TokenPair("accessToken", "refreshToken", 600));

        TokenPair tokens = service.ejecutar("admin@cafepos.com", "ClaveSegura2026");

        assertNotNull(tokens);
        assertEquals("accessToken", tokens.accessToken());
        assertEquals("refreshToken", tokens.refreshToken());
        assertEquals(0, superadmin.getIntentosFallidos());
        verify(superadminRepository).save(superadmin);
    }

    @Test
    void ejecutar_passwordIncorrecto_incrementaIntentosYLanzaExcepcion() {
        Superadmin superadmin = new Superadmin("Admin", "admin@cafepos.com", "$2a$hash");
        when(superadminRepository.findByCorreo("admin@cafepos.com")).thenReturn(Optional.of(superadmin));
        when(passwordEncoder.matches("ClaveIncorrecta", "$2a$hash")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () ->
                service.ejecutar("admin@cafepos.com", "ClaveIncorrecta"));

        assertEquals(1, superadmin.getIntentosFallidos());
        verify(superadminRepository).save(superadmin);
    }

    @Test
    void ejecutar_cincoIntentosFallidos_bloqueaCuentaYLanzaCuentaBloqueadaException() {
        Superadmin superadmin = new Superadmin("Admin", "admin@cafepos.com", "$2a$hash");
        // Simular 4 intentos previos
        for (int i = 0; i < 4; i++) {
            superadmin.registrarIntentoFallido();
        }
        assertEquals(4, superadmin.getIntentosFallidos());

        when(superadminRepository.findByCorreo("admin@cafepos.com")).thenReturn(Optional.of(superadmin));
        when(passwordEncoder.matches("ClaveIncorrecta", "$2a$hash")).thenReturn(false);

        assertThrows(CuentaBloqueadaException.class, () ->
                service.ejecutar("admin@cafepos.com", "ClaveIncorrecta"));

        assertEquals(5, superadmin.getIntentosFallidos());
        verify(superadminRepository).save(superadmin);
    }

    @Test
    void ejecutar_correoInexistente_lanzaCredencialesInvalidas() {
        when(superadminRepository.findByCorreo("desconocido@cafepos.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () ->
                service.ejecutar("desconocido@cafepos.com", "Cualquiera123"));
    }
}
