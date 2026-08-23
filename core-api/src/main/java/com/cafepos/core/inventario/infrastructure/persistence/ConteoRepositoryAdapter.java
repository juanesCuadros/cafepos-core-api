package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Conteo;
import com.cafepos.core.inventario.domain.ConteoDetalle;
import com.cafepos.core.inventario.domain.ConteoDetalleItem;
import com.cafepos.core.inventario.domain.ConteoRepository;
import com.cafepos.core.inventario.domain.ConteoResumen;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class ConteoRepositoryAdapter implements ConteoRepository {

    private final ConteoJpaRepository conteoJpaRepository;
    private final ConteoDetalleJpaRepository conteoDetalleJpaRepository;

    ConteoRepositoryAdapter(ConteoJpaRepository conteoJpaRepository,
                             ConteoDetalleJpaRepository conteoDetalleJpaRepository) {
        this.conteoJpaRepository = conteoJpaRepository;
        this.conteoDetalleJpaRepository = conteoDetalleJpaRepository;
    }

    @Override
    public Conteo guardar(Conteo conteo) {
        return conteoJpaRepository.save(conteo);
    }

    @Override
    public Optional<Conteo> buscarPorId(Integer id) {
        return conteoJpaRepository.findById(id);
    }

    @Override
    public void guardarDetalle(List<ConteoDetalle> detalles) {
        conteoDetalleJpaRepository.saveAll(detalles);
    }

    @Override
    public List<ConteoResumen> listar() {
        return conteoJpaRepository.listar().stream()
                .map(row -> new ConteoResumen(row.getId(), row.getFecha().atOffset(ZoneOffset.UTC),
                        row.getUsuarioNombre(), row.getNumInsumos(), row.getNumDiferencias()))
                .toList();
    }

    @Override
    public List<ConteoDetalleItem> detalleDe(Integer conteoId) {
        return conteoDetalleJpaRepository.detalleDe(conteoId).stream()
                .map(row -> new ConteoDetalleItem(row.getInsumoNombre(), row.getStockSistema(),
                        row.getStockFisico(), row.getDiferencia()))
                .toList();
    }
}
