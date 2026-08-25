package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.MetodoPago;
import com.cafepos.core.restaurante.domain.MetodoPagoRepository;
import com.cafepos.core.restaurante.domain.MetodoPagoResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MetodoPagoRepositoryAdapter implements MetodoPagoRepository {

    private final MetodoPagoJpaRepository jpaRepository;

    MetodoPagoRepositoryAdapter(MetodoPagoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MetodoPago guardar(MetodoPago metodoPago) {
        return jpaRepository.save(metodoPago);
    }

    @Override
    public Optional<MetodoPago> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<MetodoPagoResumen> listar() {
        return jpaRepository.listar().stream()
                .map(row -> new MetodoPagoResumen(row.getId(), row.getNombre(), row.getIcono(),
                        row.getEsEfectivo(), row.getEstado(), row.getCodigoFactus()))
                .toList();
    }

    @Override
    public void eliminar(MetodoPago metodoPago) {
        jpaRepository.delete(metodoPago);
    }
}
