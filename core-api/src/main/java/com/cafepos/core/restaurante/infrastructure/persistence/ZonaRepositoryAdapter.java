package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Zona;
import com.cafepos.core.restaurante.domain.ZonaRepository;
import com.cafepos.core.restaurante.domain.ZonaResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ZonaRepositoryAdapter implements ZonaRepository {

    private final ZonaJpaRepository jpaRepository;

    ZonaRepositoryAdapter(ZonaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Zona guardar(Zona zona) {
        return jpaRepository.save(zona);
    }

    @Override
    public Optional<Zona> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ZonaResumen> listarConNumMesas() {
        return jpaRepository.listarConNumMesas().stream()
                .map(row -> new ZonaResumen(row.getId(), row.getCodigo(), row.getIcono(), row.getNombre(),
                        row.getNumMesas(), row.getEstado()))
                .toList();
    }

    @Override
    public long contarMesas(Integer zonaId) {
        return jpaRepository.contarMesas(zonaId);
    }

    @Override
    public void eliminar(Zona zona) {
        jpaRepository.delete(zona);
    }
}
