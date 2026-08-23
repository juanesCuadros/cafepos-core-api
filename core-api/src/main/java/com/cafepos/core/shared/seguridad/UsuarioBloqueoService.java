package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * REQUIRES_NEW a proposito, mismo criterio que EventoSeguridadService: el
 * incremento de intentos_fallidos (o el reseteo en login/PIN exitoso) tiene
 * que persistir aunque el caller termine lanzando una excepcion propia
 * (CredencialesInvalidasException, PinIncorrectoException) y haciendo
 * rollback de su propia transaccion — si no, el contador nunca avanzaria y
 * el bloqueo jamas se activaria.
 *
 * Re-busca el usuario por id en vez de recibir la instancia ya cargada por
 * el caller: esa instancia pertenece al EntityManager de la transaccion
 * exterior, que esta suspendida mientras esta corre — re-buscar evita mezclar
 * entidades de dos persistence contexts distintos.
 *
 * Los metodos de PIN usan un contador independiente del de login
 * (pin_intentos_fallidos/pin_bloqueado_hasta, ver V17) — un PIN incorrecto
 * nunca debe bloquear el login ni viceversa.
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int registrarPinIntentoFallido(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        int intentosRestantes = usuario.registrarPinIntentoFallido();
        usuarioRepository.save(usuario);
        return intentosRestantes;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarPinExitoso(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        usuario.registrarPinExitoso();
        usuarioRepository.save(usuario);
    }
}
