package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * REQUIRES_NEW a proposito, mismo criterio que EventoSeguridadService: el
 * incremento de intentos_fallidos (o el reseteo en login exitoso) tiene que
 * persistir aunque LoginService termine lanzando CredencialesInvalidasException
 * y haciendo rollback de su propia transaccion — si no, el contador nunca
 * avanzaria y el bloqueo de RN-008 jamas se activaria.
 *
 * Re-busca el usuario por id en vez de recibir la instancia ya cargada por
 * LoginService: esa instancia pertenece al EntityManager de la transaccion
 * exterior, que esta suspendida mientras esta corre — re-buscar evita mezclar
 * entidades de dos persistence contexts distintos.
 */
@Service
public class UsuarioBloqueoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioBloqueoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIntentoFallido(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        usuario.registrarIntentoFallido();
        usuarioRepository.save(usuario);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarLoginExitoso(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        usuario.registrarLoginExitoso();
        usuarioRepository.save(usuario);
    }
}
