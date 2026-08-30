package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.CedulaDuplicadaException;
import com.cafepos.core.personal.domain.Empleado;
import com.cafepos.core.personal.domain.EmpleadoRepository;
import com.cafepos.core.personal.domain.EmpleadoResumen;
import com.cafepos.core.personal.domain.ResumenTurnosMes;
import com.cafepos.core.personal.domain.UsuarioAsociado;
import com.cafepos.core.shared.texto.MascaraDocumento;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class EmpleadoRepositoryAdapter implements EmpleadoRepository {

    private final EmpleadoJpaRepository jpaRepository;

    EmpleadoRepositoryAdapter(EmpleadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /** saveAndFlush (no save) a proposito — fuerza el INSERT/UPDATE real aca mismo para que el UNIQUE(tenant_id, cedula) reviente en este try, no despues. */
    @Override
    public Empleado guardar(Empleado empleado) {
        try {
            return jpaRepository.saveAndFlush(empleado);
        } catch (DataIntegrityViolationException ex) {
            throw new CedulaDuplicadaException();
        }
    }

    @Override
    public Optional<Empleado> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<EmpleadoResumen> listar(String cargo, String estado, String q) {
        return jpaRepository.listar(cargo, estado, q).stream()
                .map(row -> new EmpleadoResumen(row.getId(), row.getCodigo(), row.getNombre(),
                        MascaraDocumento.enmascarar(row.getCedula()), row.getCargo(), row.getTelefono(),
                        row.getEstado()))
                .toList();
    }

    @Override
    public void eliminar(Empleado empleado) {
        jpaRepository.delete(empleado);
    }

    @Override
    public Optional<UsuarioAsociado> buscarUsuarioAsociado(Integer empleadoId) {
        return jpaRepository.buscarUsuarioAsociado(empleadoId)
                .map(row -> new UsuarioAsociado(row.getId(), row.getCorreo(), row.getRol()));
    }

    @Override
    public ResumenTurnosMes resumenTurnosMesActual(Integer empleadoId) {
        var row = jpaRepository.resumenTurnosMesActual(empleadoId);
        return new ResumenTurnosMes(row.getTotalTurnos(), row.getHorasTrabajadas());
    }
}
