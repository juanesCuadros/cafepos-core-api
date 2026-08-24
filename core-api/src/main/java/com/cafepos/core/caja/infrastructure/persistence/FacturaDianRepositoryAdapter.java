package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.FacturaListadoItem;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class FacturaDianRepositoryAdapter implements FacturaDianRepository {

    private final FacturaDianJpaRepository jpaRepository;

    FacturaDianRepositoryAdapter(FacturaDianJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FacturaDian guardar(FacturaDian factura) {
        return jpaRepository.save(factura);
    }

    @Override
    public Optional<FacturaDian> buscarPorVentaId(Integer ventaId) {
        return jpaRepository.findByVentaId(ventaId);
    }

    @Override
    public Optional<FacturaDian> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<FacturaListadoItem> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String estadoDian,
                                            String cliente, String numeroFactura) {
        return jpaRepository.listar(fechaInicio, fechaFin, estadoDian, cliente, numeroFactura).stream()
                .map(row -> new FacturaListadoItem(row.getId(), row.getNumeroFactura(),
                        row.getFechaEmision().atOffset(ZoneOffset.UTC), row.getClienteNombre(), row.getTotal(),
                        row.getEstadoDian()))
                .toList();
    }
}
