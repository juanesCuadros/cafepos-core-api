package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.AreaCocina;
import com.cafepos.core.configuracion.domain.AreaCocinaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class AreaCocinaRepositoryAdapter implements AreaCocinaRepository {

    private final AreaCocinaJpaRepository jpaRepository;

    AreaCocinaRepositoryAdapter(AreaCocinaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AreaCocina guardar(AreaCocina areaCocina) {
        return jpaRepository.save(areaCocina);
    }

    @Override
    public Optional<AreaCocina> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AreaCocina> listar() {
        return jpaRepository.findAll(Sort.by("nombre"));
    }

    @Override
    public void eliminar(AreaCocina areaCocina) {
        jpaRepository.delete(areaCocina);
    }
}
