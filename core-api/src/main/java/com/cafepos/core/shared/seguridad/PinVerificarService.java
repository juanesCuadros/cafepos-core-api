package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PinVerificarService {

    private static final String ROL_ADMIN = "Admin";
    private static final String ROL_JEFE = "Jefe";

    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioBloqueoService usuarioBloqueoService;
    private final EventoSeguridadService eventoSeguridadService;
    private final JwtService jwtService;

    public PinVerificarService(UsuarioRepository usuarioRepository,
                                PermisoRepository permisoRepository,
                                PasswordEncoder passwordEncoder,
                                UsuarioBloqueoService usuarioBloqueoService,
                                EventoSeguridadService eventoSeguridadService,
                                JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioBloqueoService = usuarioBloqueoService;
        this.eventoSeguridadService = eventoSeguridadService;
        this.jwtService = jwtService;
    }

    /**
     * a. Correo inexistente, rol distinto de Admin/Jefe, y sin PIN
     *    configurado (pinAutorizacionHash null) devuelven la misma
     *    PinIncorrectoException generica (sin intentosRestantes) — nunca
     *    revelar cual de las tres fue, mismo criterio que LoginService.
     * b. Si ya esta bloqueado (Usuario.estaPinBloqueado), se rechaza ANTES
     *    de comparar el PIN — evita distinguir "bloqueado" de "PIN
     *    incorrecto" por tiempo de respuesta, mismo motivo que RN-008.
     * c. El incremento/reseteo de pin_intentos_fallidos corre en su propia
     *    transaccion (UsuarioBloqueoService, REQUIRES_NEW) para sobrevivir
     *    el rollback de PinIncorrectoException, igual que el contador de
     *    login.
     */
    @Transactional
    public PinAutorizacion ejecutar(String usuarioAutorizaCorreo, String pin, String modulo, String accion,
                                     String recursoTipo, Integer recursoId) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Usuario usuario = usuarioRepository.findByTenantIdAndCorreo(tenantId, usuarioAutorizaCorreo).orElse(null);
        if (usuario == null || !esAdminOJefe(usuario) || usuario.getPinAutorizacionHash() == null) {
            throw new PinIncorrectoException();
        }

        if (usuario.estaPinBloqueado()) {
            throw new PinBloqueadoException();
        }

        if (!passwordEncoder.matches(pin, usuario.getPinAutorizacionHash())) {
            int intentosRestantes = usuarioBloqueoService.registrarPinIntentoFallido(usuario.getId());
            eventoSeguridadService.registrar(tenantId, usuario.getId(),
                    EventoSeguridad.TIPO_PIN_FALLIDO, "PIN incorrecto");
            throw new PinIncorrectoException(intentosRestantes);
        }

        usuarioBloqueoService.registrarPinExitoso(usuario.getId());

        Permiso permiso = permisoRepository.findByModuloAndAccion(modulo, accion)
                .orElseThrow(PermisoNoEncontradoException::new);

        String pinToken = jwtService.issuePinStepUpToken(tenantId, usuario.getId(), permiso.getId(),
                recursoTipo, recursoId);
        return new PinAutorizacion(usuario.getId(), pinToken);
    }

    private boolean esAdminOJefe(Usuario usuario) {
        String rolNombre = usuario.getRol().getNombre();
        return ROL_ADMIN.equals(rolNombre) || ROL_JEFE.equals(rolNombre);
    }
}
