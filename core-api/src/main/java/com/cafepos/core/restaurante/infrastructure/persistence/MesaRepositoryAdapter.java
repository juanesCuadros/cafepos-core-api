package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Mesa;
import com.cafepos.core.restaurante.domain.MesaRepository;
import com.cafepos.core.restaurante.domain.MesaResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MesaRepositoryAdapter implements MesaRepository {

    private final MesaJpaRepository jpaRepository;

    MesaRepositoryAdapter(MesaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Mesa guardar(Mesa mesa) {
        return jpaRepository.save(mesa);
    }

    @Override
    public Optional<Mesa> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<MesaResumen> listarDeZona(Integer zonaId) {
        return jpaRepository.listarDeZona(zonaId).stream()
                .map(row -> new MesaResumen(row.getId(), row.getCodigo(), row.getNumero(), row.getCapacidad(),
                        row.getEstado()))
                .toList();
    }

    @Override
    public void eliminar(Mesa mesa) {
        jpaRepository.delete(mesa);
    }
}
