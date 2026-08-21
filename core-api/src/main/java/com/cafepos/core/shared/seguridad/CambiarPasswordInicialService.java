package com.cafepos.core.shared.seguridad;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarPasswordInicialService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairIssuer tokenPairIssuer;

    public CambiarPasswordInicialService(UsuarioRepository usuarioRepository,
                                          PasswordEncoder passwordEncoder,
                                          TokenPairIssuer tokenPairIssuer) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPairIssuer = tokenPairIssuer;
    }

    /**
     * Emite un par de tokens nuevo al terminar: el access token que trajo el
     * usuario todavia tiene debe_cambiar_password=true en su claim (se
     * emitio antes del cambio), asi que sin un par nuevo el mismo filtro que
     * lo dejo entrar aca seguiria bloqueando el resto de la API hasta que
     * ese token expire.
     */
    @Transactional
    public TokenPair ejecutar(Integer usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(PasswordActualIncorrectaException::new);
        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw new PasswordActualIncorrectaException();
        }
        usuario.cambiarPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return tokenPairIssuer.emitir(usuario);
    }
}
