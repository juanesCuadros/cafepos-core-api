package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.inventario.domain.Perdida;
import com.cafepos.core.inventario.domain.PerdidaRepository;
import com.cafepos.core.inventario.domain.PerdidaResultado;
import com.cafepos.core.inventario.domain.PerdidaResumen;
import com.cafepos.core.inventario.domain.StockInsuficienteException;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PerdidaService {

    private static final String REFERENCIA_TIPO_PERDIDA = "perdida";

    private final PerdidaRepository perdidaRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public PerdidaService(PerdidaRepository perdidaRepository, InsumoRepository insumoRepository,
                           MovimientoInventarioRepository movimientoInventarioRepository) {
        this.perdidaRepository = perdidaRepository;
        this.insumoRepository = insumoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<PerdidaResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaInsumoId,
                                        String motivo) {
        return perdidaRepository.listar(fechaInicio, fechaFin, categoriaInsumoId, motivo);
    }

    @Transactional
    public PerdidaResultado registrar(Integer insumoId, BigDecimal cantidad, String motivo, LocalDate fecha,
                                       String observaciones, Integer usuarioId) {
        Insumo insumo = insumoRepository.buscarPorId(insumoId).orElseThrow(InsumoNoEncontradoException::new);

        BigDecimal stockAnterior = insumo.getStockActual();
        BigDecimal stockNuevo = stockAnterior.subtract(cantidad);
        if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockInsuficienteException(stockAnterior, cantidad);
        }
        BigDecimal costoCalculado = cantidad.multiply(insumo.getCostoActual());

        insumo.actualizarStock(stockNuevo);
        insumoRepository.guardar(insumo);

        Perdida perdida = new Perdida(TenantContext.getCurrentTenantId(), insumo.getId(), usuarioId, cantidad,
                motivo, fecha, observaciones, costoCalculado);
        perdida = perdidaRepository.guardar(perdida);

        MovimientoInventario movimiento = new MovimientoInventario(insumo.getTenantId(), insumo.getId(), usuarioId,
                MovimientoInventario.TIPO_PERDIDA, cantidad, motivo, REFERENCIA_TIPO_PERDIDA, perdida.getId());
        movimientoInventarioRepository.guardar(movimiento);

        return new PerdidaResultado(perdida.getId(), costoCalculado, stockAnterior, stockNuevo);
    }
}
