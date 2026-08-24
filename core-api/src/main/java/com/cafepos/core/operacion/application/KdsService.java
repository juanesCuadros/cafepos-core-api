package com.cafepos.core.operacion.application;

import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.operacion.domain.PedidoItem;
import com.cafepos.core.operacion.domain.PedidoItemNoEncontradoException;
import com.cafepos.core.operacion.domain.PedidoItemRepository;
import com.cafepos.core.operacion.domain.PedidoRepository;
import com.cafepos.core.productosmenu.application.ComboService;
import com.cafepos.core.productosmenu.application.ProductoService;
import com.cafepos.core.productosmenu.domain.ComboParaPedido;
import com.cafepos.core.productosmenu.domain.ProductoParaPedido;
import com.cafepos.core.restaurante.application.ZonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Cocina (KDS) — Parte 3. */
@Service
public class KdsService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ZonaService zonaService;
    private final ProductoService productoService;
    private final ComboService comboService;

    public KdsService(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
                       ZonaService zonaService, ProductoService productoService, ComboService comboService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.zonaService = zonaService;
        this.productoService = productoService;
        this.comboService = comboService;
    }

    @Transactional(readOnly = true)
    public List<KdsPedidoVista> listar(String estadoFiltro) {
        return pedidoRepository.listarEnviadosOListos().stream()
                .map(pedido -> aVista(pedido, estadoFiltro))
                .filter(v -> !v.items().isEmpty())
                .toList();
    }

    @Transactional
    public KdsItemResultado cambiarEstadoItem(Integer itemId, String nuevoEstado) {
        PedidoItem item = pedidoItemRepository.buscarPorId(itemId).orElseThrow(PedidoItemNoEncontradoException::new);
        item.transicionarEstado(nuevoEstado);
        item = pedidoItemRepository.guardar(item);

        List<PedidoItem> itemsDelPedido = pedidoItemRepository.listarDePedido(item.getPedidoId());
        boolean todosListos = itemsDelPedido.stream().allMatch(PedidoItem::estaListo);
        if (todosListos) {
            pedidoRepository.buscarPorId(item.getPedidoId()).ifPresent(pedido -> {
                pedido.marcarTodosListos();
                pedidoRepository.guardar(pedido);
            });
        }
        return new KdsItemResultado(item.getId(), item.getEstadoPreparacion(), todosListos);
    }

    private KdsPedidoVista aVista(Pedido pedido, String estadoFiltro) {
        String mesaNumero = pedido.getMesaId() == null ? null
                : zonaService.buscarMesaResumenPorId(pedido.getMesaId()).map(m -> m.numero()).orElse(null);
        List<KdsItemVista> items = pedidoItemRepository.listarDePedido(pedido.getId()).stream()
                .filter(i -> estadoFiltro == null || estadoFiltro.equals(i.getEstadoPreparacion()))
                .map(this::aItemVista)
                .toList();
        return new KdsPedidoVista(pedido.getId(), pedido.getNumeroOrden(), mesaNumero, pedido.getTipo(),
                pedido.getFechaEnviado(), items);
    }

    private KdsItemVista aItemVista(PedidoItem item) {
        String nombre;
        if (item.getProductoId() != null) {
            nombre = productoService.buscarParaPedido(item.getProductoId())
                    .map(ProductoParaPedido::nombre)
                    .orElse("Producto eliminado");
        } else {
            nombre = comboService.buscarParaPedido(item.getComboId())
                    .map(ComboParaPedido::nombre)
                    .orElse("Combo eliminado");
        }
        return new KdsItemVista(item.getId(), nombre, item.getCantidad(), item.getObservacion(),
                item.getEstadoPreparacion());
    }
}
