package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.CategoriaInsumo;
import com.cafepos.core.inventario.domain.CategoriaInsumoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class CategoriaInsumoRepositoryAdapter implements CategoriaInsumoRepository {

    private final CategoriaInsumoJpaRepository jpaRepository;

    CategoriaInsumoRepositoryAdapter(CategoriaInsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CategoriaInsumo guardar(CategoriaInsumo categoriaInsumo) {
        return jpaRepository.save(categoriaInsumo);
    }

    @Override
    public Optional<CategoriaInsumo> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CategoriaInsumo> listar() {
        return jpaRepository.findAllByOrderByNombre();
    }
}
