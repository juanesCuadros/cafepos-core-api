package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.Tenant;
import com.cafepos.core.shared.tenant.TenantContext;
import com.cafepos.core.shared.tenant.TenantRepository;
import com.cafepos.core.shared.tenant.TenantSuspendidoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairIssuer tokenPairIssuer;
    private final UsuarioBloqueoService usuarioBloqueoService;
    private final EventoSeguridadService eventoSeguridadService;

    public LoginService(UsuarioRepository usuarioRepository,
                         TenantRepository tenantRepository,
                         PasswordEncoder passwordEncoder,
                         TokenPairIssuer tokenPairIssuer,
                         UsuarioBloqueoService usuarioBloqueoService,
                         EventoSeguridadService eventoSeguridadService) {
        this.usuarioRepository = usuarioRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPairIssuer = tokenPairIssuer;
        this.usuarioBloqueoService = usuarioBloqueoService;
        this.eventoSeguridadService = eventoSeguridadService;
    }

    /**
     * El tenant ya se resolvio antes de llegar aca (TenantFilter, via
     * subdominio o X-Tenant-Slug en dev) — no es un campo del body.
     *
     * a-b. Correo inexistente, password incorrecto y usuario inactivo
     *      devuelven la misma CredencialesInvalidasException — nunca
     *      revelar cual de los tres fue.
     * c.   El estado del tenant se chequea DESPUES de validar credenciales
     *      (no antes: no hay razon para filtrar el estado del negocio a
     *      quien todavia no probo ser quien dice ser) y usa una excepcion
     *      DISTINTA — el frontend necesita diferenciar "credenciales malas"
     *      de "negocio bloqueado".
     * d.   RN-008: si el usuario ya esta bloqueado (Usuario.estaBloqueado),
     *      se rechaza ANTES de siquiera comparar la password — evita que un
     *      atacante seguro de la password correcta la use para distinguir
     *      "bloqueado" de "password incorrecta" por tiempo de respuesta.
     *      El incremento/reseteo de intentos_fallidos corre en su propia
     *      transaccion (UsuarioBloqueoService, REQUIRES_NEW) porque tiene
     *      que sobrevivir el rollback de CredencialesInvalidasException.
     */
    @Transactional
    public TokenPair ejecutar(String correo, String password) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Usuario usuario = usuarioRepository.findByTenantIdAndCorreo(tenantId, correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (usuario.estaBloqueado()) {
            throw new CuentaBloqueadaException();
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            usuarioBloqueoService.registrarIntentoFallido(usuario.getId());
            eventoSeguridadService.registrar(tenantId, usuario.getId(),
                    EventoSeguridad.TIPO_LOGIN_FALLIDO, "Password incorrecta");
            throw new CredencialesInvalidasException();
        }
        if (!usuario.estaActivo()) {
            throw new CredencialesInvalidasException();
        }

        usuarioBloqueoService.registrarLoginExitoso(usuario.getId());

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(CredencialesInvalidasException::new);
        if (tenant.estaSuspendidoOCancelado()) {
            throw new TenantSuspendidoException(tenant.mensajeBloqueo());
        }

        return tokenPairIssuer.emitir(usuario);
    }
}
