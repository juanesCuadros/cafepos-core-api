package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.Impresora;
import com.cafepos.core.configuracion.domain.ImpresoraRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ImpresoraRepositoryAdapter implements ImpresoraRepository {

    private final ImpresoraJpaRepository jpaRepository;

    ImpresoraRepositoryAdapter(ImpresoraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Impresora guardar(Impresora impresora) {
        return jpaRepository.save(impresora);
    }

    @Override
    public Optional<Impresora> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Impresora> listar() {
        return jpaRepository.findAll(Sort.by("id"));
    }

    @Override
    public void eliminar(Impresora impresora) {
        jpaRepository.delete(impresora);
    }
}
