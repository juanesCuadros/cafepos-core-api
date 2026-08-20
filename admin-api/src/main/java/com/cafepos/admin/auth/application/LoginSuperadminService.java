package com.cafepos.admin.auth.application;

import com.cafepos.admin.auth.domain.CredencialesInvalidasException;
import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginSuperadminService {

    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairIssuer tokenPairIssuer;

    public LoginSuperadminService(SuperadminRepository superadminRepository,
                                   PasswordEncoder passwordEncoder,
                                   TokenPairIssuer tokenPairIssuer) {
        this.superadminRepository = superadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPairIssuer = tokenPairIssuer;
    }

    /**
     * Correo inexistente, password incorrecto y cuenta inactiva devuelven
     * la misma CredencialesInvalidasException — nunca revelar cual de los
     * tres fue.
     */
    @Transactional
    public TokenPair ejecutar(String correo, String password) {
        Superadmin superadmin = superadminRepository.findByCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(password, superadmin.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        if (!superadmin.estaActivo()) {
            throw new CredencialesInvalidasException();
        }
        return tokenPairIssuer.emitir(superadmin.getId());
    }
}
