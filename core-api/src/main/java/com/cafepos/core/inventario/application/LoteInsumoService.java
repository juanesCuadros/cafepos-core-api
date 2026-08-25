package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.LoteInsumo;
import com.cafepos.core.inventario.domain.LoteInsumoRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.compras
 * genere/agote lotes al registrar/anular una compra — este modulo no tiene
 * UI propia para escribir lote_insumo, solo lo consulta para vencimientos
 * (ver VencimientoService).
 */
@org.springframework.modulith.NamedInterface("loteInsumoService")
@Service
public class LoteInsumoService {

    private final LoteInsumoRepository loteInsumoRepository;

    public LoteInsumoService(LoteInsumoRepository loteInsumoRepository) {
        this.loteInsumoRepository = loteInsumoRepository;
    }

    /** API publica de este modulo para com.cafepos.core.compras al registrar una compra (una llamada por linea de detalle). */
    @Transactional
    public void crear(Integer insumoId, Integer compraDetalleId, String numeroLote, LocalDate fechaVencimiento,
                       BigDecimal cantidad) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        loteInsumoRepository.guardar(
                LoteInsumo.crear(tenantId, insumoId, compraDetalleId, numeroLote, fechaVencimiento, cantidad));
    }

    /**
     * API publica de este modulo para com.cafepos.core.compras al anular una
     * compra (una llamada por linea de detalle) — sin-op silencioso si el
     * lote no existe (no deberia pasar en un flujo normal, pero anular no
     * debe romperse por un lote ya inconsistente).
     */
    @Transactional
    public void agotarPorCompraDetalleId(Integer compraDetalleId) {
        loteInsumoRepository.buscarPorCompraDetalleId(compraDetalleId).ifPresent(lote -> {
            lote.agotar();
            loteInsumoRepository.guardar(lote);
        });
    }
}
