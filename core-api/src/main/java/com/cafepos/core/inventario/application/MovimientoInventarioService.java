package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.inventario.domain.MovimientoInventarioResumen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Solo lectura a proposito — los movimientos los genera el sistema (ajustes, perdidas, conteos), nunca a mano. */
@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, String tipo,
                                                      Integer insumoId, Integer usuarioId) {
        return movimientoInventarioRepository.listar(fechaInicio, fechaFin, tipo, insumoId, usuarioId);
    }
}
