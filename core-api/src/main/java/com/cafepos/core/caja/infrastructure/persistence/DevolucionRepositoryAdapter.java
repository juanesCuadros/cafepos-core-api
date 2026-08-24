package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.Devolucion;
import com.cafepos.core.caja.domain.DevolucionListadoItem;
import com.cafepos.core.caja.domain.DevolucionRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class DevolucionRepositoryAdapter implements DevolucionRepository {

    private final DevolucionJpaRepository jpaRepository;

    DevolucionRepositoryAdapter(DevolucionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Devolucion guardar(Devolucion devolucion) {
        return jpaRepository.save(devolucion);
    }

    @Override
    public Optional<Devolucion> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DevolucionListadoItem> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String estado) {
        return jpaRepository.listar(fechaInicio, fechaFin, estado).stream()
                .map(row -> new DevolucionListadoItem(row.getId(), row.getVentaCodigo(),
                        row.getFecha().atOffset(ZoneOffset.UTC), row.getClienteNombre(), row.getMontoDevuelto(),
                        row.getMetodoReembolso(), row.getEstado()))
                .toList();
    }
}
