package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Perdida;
import com.cafepos.core.inventario.domain.PerdidaRepository;
import com.cafepos.core.inventario.domain.PerdidaResumen;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
class PerdidaRepositoryAdapter implements PerdidaRepository {

    private final PerdidaJpaRepository jpaRepository;

    PerdidaRepositoryAdapter(PerdidaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Perdida guardar(Perdida perdida) {
        return jpaRepository.save(perdida);
    }

    @Override
    public List<PerdidaResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaInsumoId,
                                        String motivo) {
        return jpaRepository.listar(fechaInicio, fechaFin, categoriaInsumoId, motivo).stream()
                .map(row -> new PerdidaResumen(row.getId(), row.getFecha(), row.getInsumoNombre(),
                        row.getCantidad(), row.getMotivo(), row.getCostoCalculado(), row.getUsuarioNombre()))
                .toList();
    }
}
