package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.inventario.domain.MovimientoInventarioResumen;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Repository
class MovimientoInventarioRepositoryAdapter implements MovimientoInventarioRepository {

    private final MovimientoInventarioJpaRepository jpaRepository;

    MovimientoInventarioRepositoryAdapter(MovimientoInventarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MovimientoInventario guardar(MovimientoInventario movimiento) {
        return jpaRepository.save(movimiento);
    }

    @Override
    public List<MovimientoInventarioResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, String tipo,
                                                      Integer insumoId, Integer usuarioId) {
        return jpaRepository.listar(fechaInicio, fechaFin, tipo, insumoId, usuarioId).stream()
                .map(row -> new MovimientoInventarioResumen(row.getId(), row.getFechaHora().atOffset(ZoneOffset.UTC),
                        row.getInsumoNombre(), row.getTipo(), row.getCantidad(), row.getUnidadMedida(),
                        row.getUsuarioNombre(), row.getMotivoOrigen(), row.getReferenciaTipo(), row.getReferenciaId()))
                .toList();
    }
}
