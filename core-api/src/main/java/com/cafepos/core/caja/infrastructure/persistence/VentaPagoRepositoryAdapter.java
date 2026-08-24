package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.ResumenMetodoPago;
import com.cafepos.core.caja.domain.VentaPago;
import com.cafepos.core.caja.domain.VentaPagoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
class VentaPagoRepositoryAdapter implements VentaPagoRepository {

    private final VentaPagoJpaRepository jpaRepository;

    VentaPagoRepositoryAdapter(VentaPagoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public VentaPago guardar(VentaPago pago) {
        return jpaRepository.save(pago);
    }

    @Override
    public List<VentaPago> listarDeVenta(Integer ventaId) {
        return jpaRepository.findByVentaId(ventaId);
    }

    @Override
    public List<String> nombresMetodoPagoDeVenta(Integer ventaId) {
        return jpaRepository.nombresMetodoPagoDeVenta(ventaId);
    }

    @Override
    public BigDecimal sumaEfectivoDeJornada(Integer jornadaId) {
        return jpaRepository.sumaEfectivoDeJornada(jornadaId);
    }

    @Override
    public List<ResumenMetodoPago> resumenPorMetodoDeJornada(Integer jornadaId) {
        return jpaRepository.resumenPorMetodoDeJornada(jornadaId).stream()
                .map(row -> new ResumenMetodoPago(row.getNombre(), row.getTotal()))
                .toList();
    }
}
