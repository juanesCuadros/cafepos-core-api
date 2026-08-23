package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.Turno;
import com.cafepos.core.operacion.domain.TurnoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class TurnoRepositoryAdapter implements TurnoRepository {

    private final TurnoJpaRepository jpaRepository;

    TurnoRepositoryAdapter(TurnoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Turno guardar(Turno turno) {
        return jpaRepository.save(turno);
    }

    @Override
    public Optional<Turno> buscarActivoPorUsuario(Integer usuarioId) {
        return jpaRepository.findByUsuarioIdAndHoraFinIsNull(usuarioId);
    }
}
