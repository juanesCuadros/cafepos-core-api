package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.Conteo;
import com.cafepos.core.inventario.domain.ConteoCompleto;
import com.cafepos.core.inventario.domain.ConteoDetalle;
import com.cafepos.core.inventario.domain.ConteoDetalleInput;
import com.cafepos.core.inventario.domain.ConteoDetalleItem;
import com.cafepos.core.inventario.domain.ConteoNoEncontradoException;
import com.cafepos.core.inventario.domain.ConteoRepository;
import com.cafepos.core.inventario.domain.ConteoResumen;
import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.MovimientoInventario;
import com.cafepos.core.inventario.domain.MovimientoInventarioRepository;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConteoService {

    private static final String REFERENCIA_TIPO_CONTEO = "conteo";
    private static final String MOTIVO_AJUSTE_CONTEO = "Ajuste por conteo de inventario";

    private final ConteoRepository conteoRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ConteoService(ConteoRepository conteoRepository, InsumoRepository insumoRepository,
                          MovimientoInventarioRepository movimientoInventarioRepository,
                          UsuarioRepository usuarioRepository) {
        this.conteoRepository = conteoRepository;
        this.insumoRepository = insumoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ConteoResumen> listar() {
        return conteoRepository.listar();
    }

    @Transactional(readOnly = true)
    public ConteoCompleto obtener(Integer id) {
        Conteo conteo = conteoRepository.buscarPorId(id).orElseThrow(ConteoNoEncontradoException::new);
        List<ConteoDetalleItem> detalle = conteoRepository.detalleDe(id);
        return new ConteoCompleto(conteo.getId(), conteo.getFecha(), nombreDe(conteo.getUsuarioId()), detalle);
    }

    /**
     * Snapshot de TODOS los insumos del detalle ANTES de cualquier UPDATE
     * (loop de lectura separado del loop de escritura) — las diferencias
     * se calculan todas contra el mismo momento, no en cascada secuencial
     * donde el ajuste de un insumo pudiera influir en el siguiente.
     */
    @Transactional
    public ConteoCompleto crear(List<ConteoDetalleInput> detalleInput, Integer usuarioId) {
        Map<Integer, Insumo> insumosPorId = new LinkedHashMap<>();
        for (ConteoDetalleInput item : detalleInput) {
            Insumo insumo = insumoRepository.buscarPorId(item.insumoId())
                    .orElseThrow(InsumoNoEncontradoException::new);
            insumosPorId.put(item.insumoId(), insumo);
        }

        Integer tenantId = TenantContext.getCurrentTenantId();
        Conteo conteo = conteoRepository.guardar(new Conteo(tenantId, usuarioId));

        List<ConteoDetalle> detalles = new ArrayList<>();
        List<ConteoDetalleItem> detalleItems = new ArrayList<>();
        for (ConteoDetalleInput item : detalleInput) {
            Insumo insumo = insumosPorId.get(item.insumoId());
            BigDecimal stockSistema = insumo.getStockActual();
            BigDecimal stockFisico = item.stockFisico();

            ConteoDetalle detalle = new ConteoDetalle(tenantId, conteo.getId(), insumo.getId(), stockSistema,
                    stockFisico);
            detalles.add(detalle);
            detalleItems.add(new ConteoDetalleItem(insumo.getNombre(), stockSistema, stockFisico,
                    detalle.getDiferencia()));

            insumo.actualizarStock(stockFisico);
            insumoRepository.guardar(insumo);

            if (detalle.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                MovimientoInventario movimiento = new MovimientoInventario(tenantId, insumo.getId(), usuarioId,
                        MovimientoInventario.TIPO_AJUSTE_CONTEO, detalle.getDiferencia(), MOTIVO_AJUSTE_CONTEO,
                        REFERENCIA_TIPO_CONTEO, conteo.getId());
                movimientoInventarioRepository.guardar(movimiento);
            }
        }
        conteoRepository.guardarDetalle(detalles);

        return new ConteoCompleto(conteo.getId(), conteo.getFecha(), nombreDe(usuarioId), detalleItems);
    }

    private String nombreDe(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId).map(Usuario::getNombre).orElse(null);
    }
}
