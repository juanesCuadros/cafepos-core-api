package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.caja.domain.CajaJornadaRepository;
import com.cafepos.core.caja.domain.JornadaYaAbiertaException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class CajaJornadaRepositoryAdapter implements CajaJornadaRepository {

    private final CajaJornadaJpaRepository jpaRepository;

    CajaJornadaRepositoryAdapter(CajaJornadaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /** saveAndFlush a proposito: fuerza el chequeo del indice unico parcial YA, no al final de la transaccion. */
    @Override
    public CajaJornada guardar(CajaJornada jornada) {
        try {
            return jpaRepository.saveAndFlush(jornada);
        } catch (DataIntegrityViolationException ex) {
            throw new JornadaYaAbiertaException();
        }
    }

    @Override
    public Optional<CajaJornada> buscarAbierta() {
        return jpaRepository.findByEstado(CajaJornada.ESTADO_ABIERTA);
    }

    @Override
    public Optional<CajaJornada> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CajaJornada> listarEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        OffsetDateTime desde = fechaInicio == null ? null : fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = fechaFin == null ? null
                : fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return jpaRepository.listarEnRango(desde, hasta);
    }
}
