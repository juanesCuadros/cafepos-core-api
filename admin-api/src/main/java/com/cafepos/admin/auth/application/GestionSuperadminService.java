package com.cafepos.admin.auth.application;

import com.cafepos.admin.auditoria.application.AuditoriaAdminService;
import com.cafepos.admin.auth.domain.CredencialesInvalidasException;
import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestionSuperadminService {

    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaAdminService auditoriaService;

    public GestionSuperadminService(SuperadminRepository superadminRepository,
                                    PasswordEncoder passwordEncoder,
                                    AuditoriaAdminService auditoriaService) {
        this.superadminRepository = superadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public Superadmin obtenerPerfil(Integer superadminId) {
        return superadminRepository.findById(superadminId)
                .orElseThrow(CredencialesInvalidasException::new);
    }

    @Transactional
    public void cambiarPassword(Integer superadminId, String passwordActual, String passwordNuevo) {
        Superadmin superadmin = superadminRepository.findById(superadminId)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(passwordActual, superadmin.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        superadmin.cambiarPassword(passwordEncoder.encode(passwordNuevo));
        superadminRepository.save(superadmin);

        auditoriaService.registrar(superadminId, "cambiar_password_superadmin", "superadmin", superadminId,
                null, null, null, null);
    }
}
