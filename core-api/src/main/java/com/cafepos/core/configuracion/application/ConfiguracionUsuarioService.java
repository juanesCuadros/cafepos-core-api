package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.PinNoPermitidoException;
import com.cafepos.core.configuracion.domain.RolNoEncontradoException;
import com.cafepos.core.configuracion.domain.UsuarioDetalle;
import com.cafepos.core.configuracion.domain.UsuarioNoEncontradoException;
import com.cafepos.core.configuracion.domain.UsuarioRepository;
import com.cafepos.core.configuracion.domain.UsuarioResumen;
import com.cafepos.core.shared.seguridad.Rol;
import com.cafepos.core.shared.seguridad.RolRepository;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfiguracionUsuarioService {

    private static final String ROL_ADMIN = "Admin";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public ConfiguracionUsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumen> listar(Integer rolId, String estado) {
        return usuarioRepository.listar(rolId, estado);
    }

    @Transactional(readOnly = true)
    public UsuarioDetalle detalleDe(Integer id) {
        return usuarioRepository.detalleDe(id).orElseThrow(UsuarioNoEncontradoException::new);
    }

    @Transactional
    public Usuario crear(String nombre, String correo, String password, Integer rolId, Integer empleadoId,
                          String pin, String estado) {
        validarPin(rolId, pin);
        Integer tenantId = TenantContext.getCurrentTenantId();
        String passwordHash = passwordEncoder.encode(password);
        String pinHash = pin != null ? passwordEncoder.encode(pin) : null;
        Usuario usuario = new Usuario(tenantId, empleadoId, rolId, nombre, correo, passwordHash, pinHash, estado);
        return usuarioRepository.guardar(usuario);
    }

    /**
     * password NUNCA se acepta aca — solo cambia por el flujo propio de
     * cambio de password. Si pin viene en el body (aunque sea para
     * borrarlo con null), se revalida contra el rol EFECTIVO: el nuevo
     * rolId si vino en el mismo PATCH, o el rol actual del usuario si no.
     */
    @Transactional
    public Usuario actualizar(Integer id, String nombre, String correo, Integer rolId,
                               JsonNullable<Integer> empleadoId, JsonNullable<String> pin, String estado) {
        Usuario usuario = usuarioRepository.buscarPorId(id).orElseThrow(UsuarioNoEncontradoException::new);
        Integer rolEfectivo = rolId != null ? rolId : usuario.getRolId();
        JsonNullable<String> pinHash = JsonNullable.undefined();
        if (pin.isPresent()) {
            String pinValor = pin.get();
            if (pinValor == null) {
                pinHash = JsonNullable.of(null);
            } else {
                validarPin(rolEfectivo, pinValor);
                pinHash = JsonNullable.of(passwordEncoder.encode(pinValor));
            }
        }
        usuario.actualizar(nombre, correo, rolId, empleadoId, pinHash, estado);
        return usuarioRepository.guardar(usuario);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.buscarPorId(id).orElseThrow(UsuarioNoEncontradoException::new);
        usuario.inactivar();
        usuarioRepository.guardar(usuario);
    }

    /** Regla de negocio (Modulo 11.1): solo Jefe (es_editable=false) o el rol Admin pueden tener PIN. */
    private void validarPin(Integer rolId, String pin) {
        if (pin == null) {
            return;
        }
        Rol rol = rolRepository.findById(rolId).orElseThrow(RolNoEncontradoException::new);
        boolean permitido = !rol.isEsEditable() || ROL_ADMIN.equals(rol.getNombre());
        if (!permitido) {
            throw new PinNoPermitidoException();
        }
    }
}
