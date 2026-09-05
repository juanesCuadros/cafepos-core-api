package com.cafepos.core.operacion.application;

import com.cafepos.core.operacion.domain.Turno;
import com.cafepos.core.operacion.domain.TurnoNoActivoException;
import com.cafepos.core.operacion.domain.TurnoRepository;
import com.cafepos.core.operacion.domain.TurnoYaActivoException;
import com.cafepos.core.operacion.domain.UsuarioSinEmpleadoException;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.dao.DataIntegrityViolationException;
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

    /**
     * El chequeo de abajo es "check-then-act" sin bloqueo — dos requests
     * casi simultaneas para el mismo usuario pueden pasarlo las dos antes de
     * que ninguna confirme su INSERT (mismo problema real que ya paso con
     * mesas, ver V29__pedido_mesa_activo_unico.sql). La garantia real es el
     * indice unico parcial de V31__turno_activo_unico.sql — este chequeo
     * previo sigue sirviendo para el caso normal (dar un error legible sin
     * ni siquiera intentar el INSERT), pero si igual se pierde la carrera,
     * el INSERT choca contra el indice y Postgres tira
     * DataIntegrityViolationException — se traduce al mismo error legible en
     * vez de dejarlo escapar como 500 generico.
     */
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
        try {
            return turnoRepository.guardar(turno);
        } catch (DataIntegrityViolationException ex) {
            throw new TurnoYaActivoException();
        }
    }

    @Transactional
    public Turno cerrar(Integer usuarioId) {
        Turno turno = turnoRepository.buscarActivoPorUsuario(usuarioId).orElseThrow(TurnoNoActivoException::new);
        turno.cerrar();
        return turnoRepository.guardar(turno);
    }
}
