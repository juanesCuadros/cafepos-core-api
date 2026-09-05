package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.FacturacionDianRepository;
import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import com.cafepos.core.restaurante.domain.NumeroFacturaReservado;
import com.cafepos.core.restaurante.domain.ResolucionVigenteResumen;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class FacturacionDianRepositoryAdapter implements FacturacionDianRepository {

    private final FacturacionDianJpaRepository jpaRepository;

    FacturacionDianRepositoryAdapter(FacturacionDianJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ResolucionVigenteResumen> buscarVigenteResumen() {
        return jpaRepository.buscarVigenteResumen().map(p -> new ResolucionVigenteResumen(
                p.getPrefijo(), p.getRangoInicio(), p.getRangoFin(), p.getNumeracionActual(),
                p.getFechaExpedicion(), p.getFechaVencimiento(), p.getAmbiente(), p.getEstado()));
    }

    @Override
    public Optional<FacturacionDianResolucion> buscarVigenteConCredenciales() {
        return jpaRepository.findTopByOrderByIdDesc();
    }

    @Override
    public Optional<String> buscarEstadoConexion() {
        return jpaRepository.buscarEstadoConexionDian();
    }

    @Override
    public Optional<NumeroFacturaReservado> incrementarYReservarNumero() {
        return jpaRepository.incrementarYReservarNumero().map(p ->
                new NumeroFacturaReservado(p.getResolucionId(), p.getPrefijo(), p.getNumero()));
    }

    @Override
    public FacturacionDianResolucion guardar(FacturacionDianResolucion resolucion) {
        return jpaRepository.save(resolucion);
    }
}
