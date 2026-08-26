package com.cafepos.core.gastos.infrastructure.persistence;

import com.cafepos.core.gastos.domain.CategoriaGasto;
import com.cafepos.core.gastos.domain.CategoriaGastoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class CategoriaGastoRepositoryAdapter implements CategoriaGastoRepository {

    private final CategoriaGastoJpaRepository jpaRepository;

    CategoriaGastoRepositoryAdapter(CategoriaGastoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CategoriaGasto guardar(CategoriaGasto categoriaGasto) {
        return jpaRepository.save(categoriaGasto);
    }

    @Override
    public Optional<CategoriaGasto> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CategoriaGasto> listar() {
        return jpaRepository.findAllByOrderByNombre();
    }
}
