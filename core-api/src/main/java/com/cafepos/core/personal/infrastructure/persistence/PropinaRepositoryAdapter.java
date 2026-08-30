package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.ConfiguracionPropinaTenant;
import com.cafepos.core.personal.domain.PropinaRepository;
import com.cafepos.core.personal.domain.VentaConPropina;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
class PropinaRepositoryAdapter implements PropinaRepository {

    private final PropinaJpaRepository jpaRepository;

    PropinaRepositoryAdapter(PropinaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConfiguracionPropinaTenant obtenerConfiguracionPropina() {
        var row = jpaRepository.obtenerConfiguracionPropina();
        return new ConfiguracionPropinaTenant(row.getPropinaDestino(), row.getPropinaPctMesero());
    }

    @Override
    public List<VentaConPropina> listarVentasConPropina(Integer usuarioId, OffsetDateTime desde,
                                                          OffsetDateTime hasta) {
        return jpaRepository.listarVentasConPropina(usuarioId, desde, hasta).stream()
                .map(row -> new VentaConPropina(row.getCodigo(), row.getFecha().atOffset(ZoneOffset.UTC),
                        row.getPropina()))
                .toList();
    }
}
