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

    public LoginService(UsuarioRepository usuarioRepository,
                         TenantRepository tenantRepository,
                         PasswordEncoder passwordEncoder,
                         TokenPairIssuer tokenPairIssuer) {
        this.usuarioRepository = usuarioRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPairIssuer = tokenPairIssuer;
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
     */
    @Transactional
    public TokenPair ejecutar(String correo, String password) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Usuario usuario = usuarioRepository.findByTenantIdAndCorreo(tenantId, correo)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        if (!usuario.estaActivo()) {
            throw new CredencialesInvalidasException();
        }

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(CredencialesInvalidasException::new);
        if (tenant.estaSuspendidoOCancelado()) {
            throw new TenantSuspendidoException(tenant.mensajeBloqueo());
        }

        return tokenPairIssuer.emitir(usuario);
    }
}
