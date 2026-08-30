package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.Turno;
import com.cafepos.core.personal.domain.TurnoRepository;
import com.cafepos.core.personal.domain.TurnoResumen;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/** Prefijo "Personal": el nombre simple "TurnoRepositoryAdapter" colisiona con operacion.infrastructure.persistence.TurnoRepositoryAdapter (mismo bean id por defecto en Spring) — confirmado real. */
@Repository
class PersonalTurnoRepositoryAdapter implements TurnoRepository {

    private final PersonalTurnoJpaRepository jpaRepository;

    PersonalTurnoRepositoryAdapter(PersonalTurnoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Turno guardar(Turno turno) {
        return jpaRepository.save(turno);
    }

    @Override
    public Optional<Turno> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<TurnoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        return jpaRepository.listar(fechaInicio, fechaFin, empleadoId).stream()
                .map(row -> new TurnoResumen(row.getId(), row.getEmpleadoNombre(), row.getFecha(),
                        row.getHoraInicio() != null ? row.getHoraInicio().atOffset(ZoneOffset.UTC) : null,
                        row.getHoraFin() != null ? row.getHoraFin().atOffset(ZoneOffset.UTC) : null,
                        row.getHorasTrabajadas()))
                .toList();
    }

    @Override
    public void eliminar(Turno turno) {
        jpaRepository.delete(turno);
    }
}
