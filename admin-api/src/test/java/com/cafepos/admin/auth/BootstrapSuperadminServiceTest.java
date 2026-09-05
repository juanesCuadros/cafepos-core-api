package com.cafepos.admin.auth;

import com.cafepos.admin.auth.application.BootstrapSuperadminService;
import com.cafepos.admin.auth.domain.BootstrapNoDisponibleException;
import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapSuperadminServiceTest {

    @Mock
    private SuperadminRepository superadminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BootstrapSuperadminService service;

    @Test
    void ejecutar_cuandoTablaVacia_creaSuperadmin() {
        when(superadminRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("ClaveSegura2026")).thenReturn("$2a$10$hashSeguro");
        when(superadminRepository.save(any(Superadmin.class))).thenAnswer(inv -> inv.getArgument(0));

        Superadmin creado = service.ejecutar("Ana Superadmin", "ana@cafepos.com", "ClaveSegura2026");

        assertNotNull(creado);
        assertEquals("Ana Superadmin", creado.getNombre());
        assertEquals("ana@cafepos.com", creado.getCorreo());
        assertEquals("$2a$10$hashSeguro", creado.getPasswordHash());
        verify(superadminRepository).save(any(Superadmin.class));
    }

    @Test
    void ejecutar_cuandoYaExisteSuperadmin_lanzaBootstrapNoDisponibleException() {
        when(superadminRepository.count()).thenReturn(1L);

        assertThrows(BootstrapNoDisponibleException.class, () ->
                service.ejecutar("Otro Admin", "otro@cafepos.com", "ClaveSegura2026"));

        verify(superadminRepository, never()).save(any());
    }
}
