package com.cafepos.core.operacion.application;

import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.operacion.domain.PedidoRepository;
import com.cafepos.core.restaurante.application.ZonaService;
import com.cafepos.core.restaurante.domain.MesaResumen;
import com.cafepos.core.restaurante.domain.ZonaResumen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** GET /operacion/mesas (Parte 1) — llama a restaurante.ZonaService (NamedInterface) para zonas/mesas. */
@Service
public class MesasPanelService {

    private static final String ZONA_ESTADO_ACTIVA = "activa";
    private static final String MESA_ESTADO_DESHABILITADA = "deshabilitada";

    private final ZonaService zonaService;
    private final PedidoRepository pedidoRepository;

    public MesasPanelService(ZonaService zonaService, PedidoRepository pedidoRepository) {
        this.zonaService = zonaService;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ZonaPanel> listar() {
        return zonaService.listar().stream()
                .filter(z -> ZONA_ESTADO_ACTIVA.equals(z.estado()))
                .map(this::aZonaPanel)
                .toList();
    }

    private ZonaPanel aZonaPanel(ZonaResumen zona) {
        List<MesaPanel> mesas = zonaService.listarMesas(zona.id()).stream()
                .filter(m -> !MESA_ESTADO_DESHABILITADA.equals(m.estado()))
                .map(this::aMesaPanel)
                .toList();
        return new ZonaPanel(zona.id(), zona.codigo(), zona.icono(), zona.nombre(), mesas);
    }

    private MesaPanel aMesaPanel(MesaResumen mesa) {
        Optional<Pedido> activo = pedidoRepository.buscarActivoPorMesa(mesa.id());
        Integer pedidoId = activo.map(Pedido::getId).orElse(null);
        var ocupadaDesde = activo.map(Pedido::getFechaApertura).orElse(null);
        return new MesaPanel(mesa.id(), mesa.codigo(), mesa.numero(), mesa.capacidad(), mesa.estado(), pedidoId,
                ocupadaDesde);
    }
}
