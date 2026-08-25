package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.inventario.domain.MovimientoInventarioResumen;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Solo lectura para su propia UI (los ajustes/perdidas/conteos generan sus
 * movimientos directo, nunca a mano) salvo dos excepciones puntuales,
 * @NamedInterface para com.cafepos.core.compras: registrarEntrada (al
 * registrar una compra) y registrarSalida (al anularla) — ver sus Javadoc.
 */
@org.springframework.modulith.NamedInterface("movimientoInventarioService")
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

    /** API publica de este modulo para com.cafepos.core.compras al registrar una compra (una llamada por linea de detalle). */
    @Transactional
    public void registrarEntrada(Integer insumoId, Integer usuarioId, BigDecimal cantidad, String referenciaTipo,
                                  Integer referenciaId) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        movimientoInventarioRepository.guardar(new MovimientoInventario(tenantId, insumoId, usuarioId,
                MovimientoInventario.TIPO_ENTRADA, cantidad, null, referenciaTipo, referenciaId));
    }

    /** API publica de este modulo para com.cafepos.core.compras al anular una compra (una llamada por linea de detalle). */
    @Transactional
    public void registrarSalida(Integer insumoId, Integer usuarioId, BigDecimal cantidad, String motivoOrigen,
                                 String referenciaTipo, Integer referenciaId) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        movimientoInventarioRepository.guardar(new MovimientoInventario(tenantId, insumoId, usuarioId,
                MovimientoInventario.TIPO_SALIDA, cantidad, motivoOrigen, referenciaTipo, referenciaId));
    }
}
