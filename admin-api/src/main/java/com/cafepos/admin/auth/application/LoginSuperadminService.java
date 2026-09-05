package com.cafepos.admin.auth.application;

import com.cafepos.admin.auth.domain.CuentaBloqueadaException;
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
     * Valida credenciales, aplica bloqueo temporal tras 5 intentos fallidos
     * consecutivos (30 min) y resetea el contador tras login exitoso.
     */
    @Transactional
    public TokenPair ejecutar(String correo, String password) {
        Superadmin superadmin = superadminRepository.findByCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (superadmin.estaBloqueado()) {
            throw new CuentaBloqueadaException();
        }

        if (!passwordEncoder.matches(password, superadmin.getPasswordHash())) {
            superadmin.registrarIntentoFallido();
            superadminRepository.save(superadmin);
            if (superadmin.estaBloqueado()) {
                throw new CuentaBloqueadaException();
            }
            throw new CredencialesInvalidasException();
        }

        if (!superadmin.estaActivo()) {
            throw new CredencialesInvalidasException();
        }

        superadmin.resetearIntentosFallidos();
        superadminRepository.save(superadmin);

        return tokenPairIssuer.emitir(superadmin.getId());
    }
}
