package com.cafepos.core.gastos.infrastructure.persistence;

import com.cafepos.core.gastos.domain.Gasto;
import com.cafepos.core.gastos.domain.GastoRepository;
import com.cafepos.core.gastos.domain.GastoResumen;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
class GastoRepositoryAdapter implements GastoRepository {

    private final GastoJpaRepository jpaRepository;

    GastoRepositoryAdapter(GastoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Gasto guardar(Gasto gasto) {
        return jpaRepository.save(gasto);
    }

    @Override
    public Optional<Gasto> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<GastoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaGastoId,
                                      String metodoPago) {
        return jpaRepository.listar(fechaInicio, fechaFin, categoriaGastoId, metodoPago).stream()
                .map(row -> new GastoResumen(row.getId(), row.getCodigo(), row.getFecha(), row.getCategoria(),
                        row.getDescripcion(), row.getMonto(), row.getMetodoPago(), row.getUsuario()))
                .toList();
    }

    @Override
    public void eliminar(Gasto gasto) {
        jpaRepository.delete(gasto);
    }
}
