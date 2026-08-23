package com.cafepos.core.operacion.application;

import com.cafepos.core.operacion.domain.Turno;
import com.cafepos.core.operacion.domain.TurnoNoActivoException;
import com.cafepos.core.operacion.domain.TurnoRepository;
import com.cafepos.core.operacion.domain.TurnoYaActivoException;
import com.cafepos.core.operacion.domain.UsuarioSinEmpleadoException;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Registrar/Cerrar turno — Parte 4. */
@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    public TurnoService(TurnoRepository turnoRepository, UsuarioRepository usuarioRepository) {
        this.turnoRepository = turnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Turno> actual(Integer usuarioId) {
        return turnoRepository.buscarActivoPorUsuario(usuarioId);
    }

    @Transactional
    public Turno iniciar(Integer usuarioId) {
        if (turnoRepository.buscarActivoPorUsuario(usuarioId).isPresent()) {
            throw new TurnoYaActivoException();
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        if (usuario.getEmpleadoId() == null) {
            throw new UsuarioSinEmpleadoException();
        }
        Turno turno = new Turno(TenantContext.getCurrentTenantId(), usuario.getEmpleadoId(), usuarioId);
        return turnoRepository.guardar(turno);
    }

    @Transactional
    public Turno cerrar(Integer usuarioId) {
        Turno turno = turnoRepository.buscarActivoPorUsuario(usuarioId).orElseThrow(TurnoNoActivoException::new);
        turno.cerrar();
        return turnoRepository.guardar(turno);
    }
}
