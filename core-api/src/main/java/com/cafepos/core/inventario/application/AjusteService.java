package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.AjusteResultado;
import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.inventario.domain.StockInsuficienteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * PENDIENTE (a proposito, NO implementado todavia): el contrato pide PIN
 * (accion "ajuste_inventario") para este endpoint. La infraestructura de
 * PIN de step-up todavia no existe en codigo (solo diseñada) - se
 * construira antes de Caja, que tiene la mayoria de acciones que la
 * requieren. usuarioAutorizaId igual se guarda en el movimiento (para
 * trazabilidad), simplemente no se valida ningun PIN todavia.
 */
@Service
public class AjusteService {

    private static final String TIPO_ENTRADA = "entrada";
    private static final String REFERENCIA_TIPO_AJUSTE = "ajuste";

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public AjusteService(InsumoRepository insumoRepository,
                          MovimientoInventarioRepository movimientoInventarioRepository) {
        this.insumoRepository = insumoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Transactional
    public AjusteResultado ajustar(Integer insumoId, String tipo, BigDecimal cantidad, String motivo,
                                    Integer usuarioAutorizaId) {
        Insumo insumo = insumoRepository.buscarPorId(insumoId).orElseThrow(InsumoNoEncontradoException::new);

        BigDecimal stockAnterior = insumo.getStockActual();
        BigDecimal delta = TIPO_ENTRADA.equals(tipo) ? cantidad : cantidad.negate();
        BigDecimal stockNuevo = stockAnterior.add(delta);
        if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockInsuficienteException(stockAnterior, cantidad);
        }

        insumo.actualizarStock(stockNuevo);
        insumoRepository.guardar(insumo);

        // tipo del movimiento SIEMPRE 'ajuste' — 'entrada'/'salida' del request es solo direccion,
        // esos valores del enum de movimiento_inventario estan reservados para compras/ventas.
        MovimientoInventario movimiento = new MovimientoInventario(insumo.getTenantId(), insumo.getId(),
                usuarioAutorizaId, MovimientoInventario.TIPO_AJUSTE, delta, motivo, REFERENCIA_TIPO_AJUSTE, null);
        movimiento = movimientoInventarioRepository.guardar(movimiento);
        movimiento.autorreferenciar();
        movimiento = movimientoInventarioRepository.guardar(movimiento);

        return new AjusteResultado(movimiento.getId(), insumo.getId(), stockAnterior, stockNuevo);
    }
}
