package com.cafepos.admin.auth.application;

import com.cafepos.admin.auth.domain.BootstrapNoDisponibleException;
import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootstrapSuperadminService {

    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapSuperadminService(SuperadminRepository superadminRepository, PasswordEncoder passwordEncoder) {
        this.superadminRepository = superadminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * SERIALIZABLE a proposito: el endpoint es de un solo uso para siempre,
     * el count()==0 + insert tiene que ser atomico frente a dos bootstraps
     * concurrentes, no solo frente a llamadas secuenciales.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Superadmin ejecutar(String nombre, String correo, String password) {
        if (superadminRepository.count() > 0) {
            throw new BootstrapNoDisponibleException();
        }
        Superadmin superadmin = new Superadmin(nombre, correo, passwordEncoder.encode(password));
        return superadminRepository.save(superadmin);
    }
}
