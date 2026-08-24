package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Devolucion;
import com.cafepos.core.caja.domain.DevolucionItem;
import com.cafepos.core.caja.domain.DevolucionItemRepository;
import com.cafepos.core.caja.domain.DevolucionListadoItem;
import com.cafepos.core.caja.domain.DevolucionNoEncontradaException;
import com.cafepos.core.caja.domain.DevolucionRepository;
import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.caja.domain.NotaCreditoRepository;
import com.cafepos.core.caja.domain.PedidoItemNoEncontradoException;
import com.cafepos.core.caja.domain.PedidoNoEncontradoException;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteRef;
import com.cafepos.core.operacion.application.PedidoService;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Devoluciones (api_03_caja.md 3.7). El PIN de step-up (ver controller) usa
 * modulo=caja.devoluciones, accion=autorizar — el catalogo marca
 * requiere_pin=true tanto en 'solicitar' como en 'autorizar', pero
 * rol_permiso solo le da 'autorizar' a Admin/Jefe (Cajero tiene 'solicitar'
 * sin PIN adicional propio) — 'autorizar' es la que efectivamente protege
 * la mutacion en este flujo de un solo paso.
 */
@Service
public class DevolucionService {

    /** Ver operacion.domain.PedidoItem.ESTADO_EN_PREPARACION/ESTADO_LISTO — no expuestos cross-module, se repiten aca. */
    private static final Set<String> ESTADOS_ITEM_YA_PREPARADO = Set.of("en_preparacion", "listo");

    private final DevolucionRepository devolucionRepository;
    private final DevolucionItemRepository devolucionItemRepository;
    private final VentaRepository ventaRepository;
    private final FacturaDianRepository facturaDianRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public DevolucionService(DevolucionRepository devolucionRepository,
                              DevolucionItemRepository devolucionItemRepository, VentaRepository ventaRepository,
                              FacturaDianRepository facturaDianRepository, NotaCreditoRepository notaCreditoRepository,
                              PedidoService pedidoService, ClienteService clienteService) {
        this.devolucionRepository = devolucionRepository;
        this.devolucionItemRepository = devolucionItemRepository;
        this.ventaRepository = ventaRepository;
        this.facturaDianRepository = facturaDianRepository;
        this.notaCreditoRepository = notaCreditoRepository;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    public List<DevolucionListadoItem> listar(LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        OffsetDateTime desde = fechaInicio == null ? null : fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = fechaFin == null ? null : fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return devolucionRepository.listar(desde, hasta, estado);
    }

    @Transactional(readOnly = true)
    public DevolucionDetalleVista detalle(Integer id) {
        Devolucion devolucion = buscarDevolucion(id);
        Venta venta = buscarVenta(devolucion.getVentaId());
        ClienteRef cliente = venta.getClienteId() == null ? null
                : clienteService.buscarParaVenta(venta.getClienteId()).orElse(null);
        Map<Integer, PedidoItemParaVenta> itemsPedido = itemsPorId(venta.getPedidoId());
        List<DevolucionItemDetalle> items = devolucionItemRepository.listarDeDevolucion(id).stream()
                .map(item -> aItemDetalle(item, itemsPedido))
                .toList();
        return new DevolucionDetalleVista(devolucion, venta.getCodigo(), cliente, items);
    }

    private DevolucionItemDetalle aItemDetalle(DevolucionItem item, Map<Integer, PedidoItemParaVenta> itemsPedido) {
        PedidoItemParaVenta pedidoItem = itemsPedido.get(item.getPedidoItemId());
        String nombre = pedidoItem == null ? null : pedidoItem.nombre();
        BigDecimal precioUnitario = pedidoItem == null ? null : pedidoItem.precioUnitario();
        BigDecimal subtotal = precioUnitario == null ? null : precioUnitario.multiply(item.getCantidad());
        return new DevolucionItemDetalle(item.getPedidoItemId(), nombre, item.getCantidad(), precioUnitario,
                subtotal);
    }

    /**
     * LOGICA CENTRAL (RN-023/024): si algun item devuelto ya estaba
     * en_preparacion/listo, TODO el reembolso va a saldo_favor del cliente —
     * salvo que la venta sea de consumidor final (sin cliente_id), caso no
     * cubierto explicitamente por el contrato: cae a pago_original (mejor
     * interpretacion razonable, no hay a quien acreditarle saldo). Si todos
     * los items devueltos seguian 'pendiente', metodo_reembolso=pago_original
     * sin reversion real a ninguna pasarela de pago (limitacion de esta
     * version, queda solo registrado).
     */
    @Transactional
    public DevolucionResultado solicitar(Integer ventaId, List<ItemDevolucionInput> items, String motivo,
                                          Integer usuarioAutorizaId) {
        Venta venta = buscarVenta(ventaId);
        Map<Integer, PedidoItemParaVenta> itemsPedido = itemsPorId(venta.getPedidoId());

        BigDecimal montoDevuelto = BigDecimal.ZERO;
        boolean algunoYaPreparado = false;
        for (ItemDevolucionInput itemInput : items) {
            PedidoItemParaVenta pedidoItem = itemsPedido.get(itemInput.pedidoItemId());
            if (pedidoItem == null) {
                throw new PedidoItemNoEncontradoException();
            }
            montoDevuelto = montoDevuelto.add(pedidoItem.precioUnitario().multiply(itemInput.cantidad()));
            if (ESTADOS_ITEM_YA_PREPARADO.contains(pedidoItem.estadoPreparacion())) {
                algunoYaPreparado = true;
            }
        }

        String metodoReembolso;
        String detalle;
        if (algunoYaPreparado && venta.getClienteId() != null) {
            metodoReembolso = Devolucion.METODO_SALDO_FAVOR;
            detalle = "Reembolsado a saldo a favor del cliente porque uno o mas items ya estaban en preparacion o listos";
        } else if (algunoYaPreparado) {
            metodoReembolso = Devolucion.METODO_PAGO_ORIGINAL;
            detalle = "Reembolsado al metodo de pago original porque la venta no tiene cliente asociado para "
                    + "acreditar saldo a favor";
        } else {
            metodoReembolso = Devolucion.METODO_PAGO_ORIGINAL;
            detalle = "Reembolsado al metodo de pago original porque el pedido no habia sido preparado aun";
        }

        Integer tenantId = TenantContext.getCurrentTenantId();
        Devolucion devolucion = new Devolucion(tenantId, ventaId, usuarioAutorizaId, motivo, montoDevuelto,
                metodoReembolso);
        devolucion = devolucionRepository.guardar(devolucion);

        for (ItemDevolucionInput itemInput : items) {
            devolucionItemRepository.guardar(new DevolucionItem(tenantId, devolucion.getId(),
                    itemInput.pedidoItemId(), itemInput.cantidad()));
        }

        if (Devolucion.METODO_SALDO_FAVOR.equals(metodoReembolso)) {
            clienteService.acreditarSaldoFavorPorDevolucion(venta.getClienteId(), montoDevuelto, devolucion.getId(),
                    usuarioAutorizaId);
        }

        Optional<FacturaDian> factura = facturaDianRepository.buscarPorVentaId(ventaId);
        boolean notaCreditoGenerada = false;
        Integer notaCreditoId = null;
        if (factura.isPresent()) {
            NotaCredito notaCredito = new NotaCredito(tenantId, factura.get().getId(), devolucion.getId(), motivo,
                    montoDevuelto);
            notaCredito = notaCreditoRepository.guardar(notaCredito);
            notaCreditoGenerada = true;
            notaCreditoId = notaCredito.getId();
        }

        return new DevolucionResultado(devolucion, notaCreditoGenerada, notaCreditoId, detalle);
    }

    private Map<Integer, PedidoItemParaVenta> itemsPorId(Integer pedidoId) {
        return pedidoService.buscarParaVenta(pedidoId).map(p -> p.items())
                .orElseThrow(PedidoNoEncontradoException::new).stream()
                .collect(java.util.stream.Collectors.toMap(PedidoItemParaVenta::id, Function.identity()));
    }

    private Devolucion buscarDevolucion(Integer id) {
        return devolucionRepository.buscarPorId(id).orElseThrow(DevolucionNoEncontradaException::new);
    }

    private Venta buscarVenta(Integer id) {
        return ventaRepository.buscarPorId(id).orElseThrow(VentaNoEncontradaException::new);
    }
}
