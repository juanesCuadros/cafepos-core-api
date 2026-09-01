package com.cafepos.core.operacion.application;

import com.cafepos.core.operacion.domain.ComboNoEncontradoException;
import com.cafepos.core.operacion.domain.ComboSeleccionIncompletaException;
import com.cafepos.core.operacion.domain.ItemInvalidoException;
import com.cafepos.core.operacion.domain.MesaDestinoNoDisponibleException;
import com.cafepos.core.operacion.domain.MesaIdNoPermitidoException;
import com.cafepos.core.operacion.domain.MesaIdObligatorioException;
import com.cafepos.core.operacion.domain.MesaNoEncontradaException;
import com.cafepos.core.operacion.domain.MesaOcupadaException;
import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.operacion.domain.PedidoItem;
import com.cafepos.core.operacion.domain.PedidoItemComboSeleccion;
import com.cafepos.core.operacion.domain.PedidoItemNoEncontradoException;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.operacion.domain.PedidoItemRepository;
import com.cafepos.core.operacion.domain.PedidoNoEncontradoException;
import com.cafepos.core.operacion.domain.PedidoParaVenta;
import com.cafepos.core.operacion.domain.PedidoRepository;
import com.cafepos.core.operacion.domain.PedidoSinMesaException;
import com.cafepos.core.operacion.domain.ProductoAgotadoException;
import com.cafepos.core.operacion.domain.ProductoNoEncontradoException;
import com.cafepos.core.configuracion.application.ConfiguracionSistemaService;
import com.cafepos.core.productosmenu.application.ComboService;
import com.cafepos.core.productosmenu.application.ProductoService;
import com.cafepos.core.productosmenu.application.PromocionService;
import com.cafepos.core.productosmenu.domain.ComboGrupoParaPedido;
import com.cafepos.core.productosmenu.domain.ComboParaPedido;
import com.cafepos.core.productosmenu.domain.ItemParaPromocion;
import com.cafepos.core.productosmenu.domain.ProductoParaPedido;
import com.cafepos.core.productosmenu.domain.PromocionSugerida;
import com.cafepos.core.restaurante.application.ZonaService;
import com.cafepos.core.restaurante.domain.MesaResumen;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import com.cafepos.core.shared.websocket.NotificacionesWebSocketService;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio principal de "pedido abierto" (Parte 2). Llama directamente
 * (sincrono, misma transaccion) a ZonaService (restaurante),
 * ProductoService/ComboService/PromocionService (productosmenu) y
 * ConfiguracionSistemaService (configuracion) — todos NamedInterface, ver
 * package-info.java de este modulo.
 *
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.caja
 * cobre un pedido (ver buscarParaVenta/marcarCerrado/finalizarEntrega) —
 * solo cruzan PedidoParaVenta/PedidoItemParaVenta (tambien anotados),
 * nunca las entidades Pedido/PedidoItem completas.
 */
@org.springframework.modulith.NamedInterface("pedidoService")
@Service
public class PedidoService {

    /** Valores de mesa.estado (ver restaurante.domain.Mesa, no expuesto — CHECK constraint en V1__schema_v4.sql). */
    private static final String MESA_ESTADO_LIBRE = "libre";
    private static final String MESA_ESTADO_OCUPADA = "ocupada";
    private static final String MESA_ESTADO_LISTA_COBRAR = "lista_cobrar";

    /** Valor de producto.estado (ver productosmenu.domain.Producto, no expuesto). */
    private static final String PRODUCTO_ESTADO_AGOTADO = "agotado";

    private static final String PREFIJO_CODIGO_ORDEN = "ORD";

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ZonaService zonaService;
    private final ProductoService productoService;
    private final ComboService comboService;
    private final PromocionService promocionService;
    private final ConfiguracionSistemaService configuracionSistemaService;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionesWebSocketService notificacionesWebSocketService;

    public PedidoService(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
                          ZonaService zonaService, ProductoService productoService, ComboService comboService,
                          PromocionService promocionService,
                          ConfiguracionSistemaService configuracionSistemaService,
                          UsuarioRepository usuarioRepository,
                          NotificacionesWebSocketService notificacionesWebSocketService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.zonaService = zonaService;
        this.productoService = productoService;
        this.comboService = comboService;
        this.promocionService = promocionService;
        this.configuracionSistemaService = configuracionSistemaService;
        this.usuarioRepository = usuarioRepository;
        this.notificacionesWebSocketService = notificacionesWebSocketService;
    }

    @Transactional
    public PedidoDetalle abrir(Integer mesaId, String tipo, Integer usuarioId) {
        if (Pedido.TIPO_MESA.equals(tipo)) {
            if (mesaId == null) {
                throw new MesaIdObligatorioException();
            }
            return abrirDeMesa(mesaId, usuarioId);
        }
        if (mesaId != null) {
            throw new MesaIdNoPermitidoException();
        }
        return abrirVentaRapida(usuarioId);
    }

    /**
     * El chequeo de abajo (buscarActivoPorMesa) es "check-then-act" sin
     * bloqueo — dos requests casi simultaneos para la misma mesa pueden
     * pasarlo los dos antes de que ninguno confirme su INSERT (confirmado
     * real en produccion, 01-sep-2026: rompio GET /operacion/mesas entero
     * para el tenant, ver V29__pedido_mesa_activo_unico.sql). La garantia
     * real es el indice unico parcial de esa migracion — este chequeo previo
     * sigue sirviendo para el caso normal (dar MesaOcupadaException con un
     * mensaje legible sin ni siquiera intentar el INSERT), pero si igual se
     * pierde la carrera, el INSERT choca contra el indice y Postgres tira
     * DataIntegrityViolationException — se traduce al mismo error legible en
     * vez de dejarlo escapar como 500 generico.
     */
    private PedidoDetalle abrirDeMesa(Integer mesaId, Integer usuarioId) {
        zonaService.buscarMesaResumenPorId(mesaId).orElseThrow(MesaNoEncontradaException::new);
        pedidoRepository.buscarActivoPorMesa(mesaId).ifPresent(p -> {
            throw new MesaOcupadaException();
        });

        Pedido pedido;
        try {
            pedido = crearPedido(mesaId, Pedido.TIPO_MESA, usuarioId);
        } catch (DataIntegrityViolationException ex) {
            throw new MesaOcupadaException();
        }
        zonaService.cambiarEstadoMesa(mesaId, MESA_ESTADO_OCUPADA);
        return construirDetalle(pedido);
    }

    /** venta_rapida (Caja) — pedido sin mesa, ver api_03_caja.md 3.1. */
    private PedidoDetalle abrirVentaRapida(Integer usuarioId) {
        Pedido pedido = crearPedido(null, Pedido.TIPO_VENTA_RAPIDA, usuarioId);
        return construirDetalle(pedido);
    }

    private Pedido crearPedido(Integer mesaId, String tipo, Integer usuarioId) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Pedido pedido = new Pedido(tenantId, mesaId, usuarioId, tipo);
        pedido = pedidoRepository.guardar(pedido);
        pedido.asignarNumeroOrden(GeneradorCodigo.generar(PREFIJO_CODIGO_ORDEN, pedido.getId()));
        return pedidoRepository.guardar(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoDetalle obtenerDetalle(Integer pedidoId) {
        return construirDetalle(buscarPedido(pedidoId));
    }

    @Transactional
    public ItemAgregado agregarItem(Integer pedidoId, Integer productoId, Integer comboId, BigDecimal cantidad,
                                     String observacion, List<SeleccionCombo> selecciones) {
        Pedido pedido = buscarPedido(pedidoId);
        if ((productoId == null) == (comboId == null)) {
            throw new ItemInvalidoException();
        }

        PedidoItem item;
        if (productoId != null) {
            item = agregarItemProducto(pedido, productoId, cantidad, observacion);
        } else {
            item = agregarItemCombo(pedido, comboId, cantidad, observacion, selecciones);
        }
        return new ItemAgregado(aDetalle(item), recalcularSubtotal(pedidoId));
    }

    @Transactional
    public PedidoItemDetalle editarItem(Integer pedidoId, Integer itemId, BigDecimal cantidad,
                                         JsonNullable<String> observacion) {
        PedidoItem item = buscarItemDePedido(pedidoId, itemId);
        item.actualizar(cantidad, observacion);
        item = pedidoItemRepository.guardar(item);
        return aDetalle(item);
    }

    /** El chequeo de PIN corre ANTES de esto, en el controller (ver PinStepUpService). */
    @Transactional
    @Auditable(entidadTipo = "pedido_item", accion = "eliminar", entidadIdExpression = "#itemId")
    public BigDecimal eliminarItem(Integer pedidoId, Integer itemId) {
        PedidoItem item = buscarItemDePedido(pedidoId, itemId);
        AuditoriaContext.registrarAntes(item);
        pedidoItemRepository.eliminar(item);
        return recalcularSubtotal(pedidoId);
    }

    @Transactional
    public EnviarComandaResultado enviarComanda(Integer pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        List<PedidoItem> items = pedidoItemRepository.listarDePedido(pedidoId);
        pedido.enviarComanda();
        pedidoRepository.guardar(pedido);
        String modo = configuracionSistemaService.obtenerModoComanda();
        notificacionesWebSocketService.kdsActualizado(TenantContext.getCurrentTenantId());
        return new EnviarComandaResultado(modo, items.stream().map(PedidoItem::getId).toList());
    }

    @Transactional
    public MoverMesaResultado moverMesa(Integer pedidoId, Integer mesaDestinoId) {
        Pedido pedido = buscarPedido(pedidoId);
        Integer mesaAnteriorId = pedido.getMesaId();

        MesaResumen destino = zonaService.buscarMesaResumenPorId(mesaDestinoId)
                .orElseThrow(MesaNoEncontradaException::new);
        if (!MESA_ESTADO_LIBRE.equals(destino.estado())) {
            throw new MesaDestinoNoDisponibleException();
        }

        pedido.moverA(mesaDestinoId);
        pedidoRepository.guardar(pedido);
        zonaService.cambiarEstadoMesa(mesaDestinoId, MESA_ESTADO_OCUPADA);
        if (mesaAnteriorId != null) {
            zonaService.cambiarEstadoMesa(mesaAnteriorId, MESA_ESTADO_LIBRE);
        }
        return new MoverMesaResultado(mesaAnteriorId, mesaDestinoId);
    }

    /** Tambien invocado internamente por prefactura() — ver contrato api_02_operacion.md. */
    @Transactional
    public Integer marcarListaCobrar(Integer pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        if (pedido.getMesaId() == null) {
            throw new PedidoSinMesaException();
        }
        zonaService.cambiarEstadoMesa(pedido.getMesaId(), MESA_ESTADO_LISTA_COBRAR)
                .orElseThrow(MesaNoEncontradaException::new);
        return pedido.getMesaId();
    }

    @Transactional
    public PrefacturaResultado prefactura(Integer pedidoId) {
        marcarListaCobrar(pedidoId);
        PedidoDetalle detalle = construirDetalle(buscarPedido(pedidoId));
        return new PrefacturaResultado(detalle.numeroOrden(), detalle.mesa().numero(), detalle.items(),
                detalle.subtotal(), OffsetDateTime.now());
    }

    /** API publica de este modulo para POST /ventas (com.cafepos.core.caja). */
    @Transactional(readOnly = true)
    public Optional<PedidoParaVenta> buscarParaVenta(Integer pedidoId) {
        return pedidoRepository.buscarPorId(pedidoId).map(pedido -> {
            List<PedidoItemParaVenta> items = pedidoItemRepository.listarDePedido(pedidoId).stream()
                    .map(this::aItemParaVenta)
                    .toList();
            return new PedidoParaVenta(pedido.getId(), pedido.getTipo(), pedido.getEstado(), pedido.getMesaId(),
                    pedido.getUsuarioId(), items);
        });
    }

    /**
     * API publica de este modulo para POST /ventas (com.cafepos.core.caja).
     * Libera la mesa de inmediato si el pedido era de tipo 'mesa' — decision
     * explicita del usuario (31-ago-2026, ver INTEGRACION.md hallazgo 3.40):
     * antes la mesa solo se liberaba en POST /ventas/{id}/finalizar-entrega,
     * un paso separado y opcional (elegir "imprimir" o "correo") que
     * dependia de que el navegador completara window.print() — un dialogo
     * nativo que el cajero puede cancelar por cualquier motivo, dejando la
     * mesa ocupada indefinidamente aunque el pago ya estuviera confirmado.
     * Cobrar y liberar la mesa pasan a ser la misma accion; como se entrega
     * el comprobante queda como paso totalmente independiente que nunca
     * bloquea nada (ver finalizarEntrega mas abajo).
     */
    @Transactional
    public void marcarCerrado(Integer pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        pedido.cerrar();
        pedidoRepository.guardar(pedido);
        if (pedido.getMesaId() != null) {
            zonaService.cambiarEstadoMesa(pedido.getMesaId(), MESA_ESTADO_LIBRE);
        }
    }

    /**
     * API publica de este modulo para POST /ventas/{id}/finalizar-entrega
     * (com.cafepos.core.caja). YA NO libera la mesa — eso pasa a ocurrir en
     * marcarCerrado() de una vez, en el momento del cobro (ver su Javadoc,
     * hallazgo 3.40). Este metodo queda puramente informativo: confirma si
     * el pedido era de tipo 'mesa' (para que la respuesta siga indicando
     * `mesa_liberada` sin mentir, aunque la liberacion real ya haya pasado
     * antes) — no muta nada.
     */
    @Transactional(readOnly = true)
    public boolean finalizarEntrega(Integer pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        return pedido.getMesaId() != null;
    }

    private PedidoItemParaVenta aItemParaVenta(PedidoItem item) {
        String nombre;
        String tasaImpuesto;
        String codigo;
        if (item.getProductoId() != null) {
            ProductoParaPedido producto = productoService.buscarParaPedido(item.getProductoId()).orElse(null);
            nombre = producto != null ? producto.nombre() : "Producto eliminado";
            tasaImpuesto = producto != null ? producto.tasaImpuesto() : null;
            codigo = producto != null ? producto.codigo() : null;
        } else {
            ComboParaPedido combo = comboService.buscarParaPedido(item.getComboId()).orElse(null);
            nombre = combo != null ? combo.nombre() : "Combo eliminado";
            tasaImpuesto = null;
            codigo = combo != null ? combo.codigo() : null;
        }
        return new PedidoItemParaVenta(item.getId(), item.getProductoId(), item.getComboId(), nombre,
                item.getCantidad(), item.getPrecioUnitario(), tasaImpuesto, item.getEstadoPreparacion(), codigo);
    }

    private PedidoItem agregarItemProducto(Pedido pedido, Integer productoId, BigDecimal cantidad,
                                            String observacion) {
        ProductoParaPedido producto = productoService.buscarParaPedido(productoId)
                .orElseThrow(ProductoNoEncontradoException::new);
        if (PRODUCTO_ESTADO_AGOTADO.equals(producto.estado())) {
            throw new ProductoAgotadoException();
        }
        PedidoItem item = new PedidoItem(pedido.getTenantId(), pedido.getId(), productoId, null,
                producto.areaCocinaId(), cantidad, observacion, producto.precioVenta());
        return pedidoItemRepository.guardar(item);
    }

    private PedidoItem agregarItemCombo(Pedido pedido, Integer comboId, BigDecimal cantidad, String observacion,
                                         List<SeleccionCombo> selecciones) {
        ComboParaPedido combo = comboService.buscarParaPedido(comboId).orElseThrow(ComboNoEncontradoException::new);
        List<SeleccionCombo> seleccionesSeguras = selecciones == null ? List.of() : selecciones;
        for (ComboGrupoParaPedido grupo : combo.grupos()) {
            SeleccionCombo seleccion = seleccionesSeguras.stream()
                    .filter(s -> grupo.id().equals(s.comboGrupoId()))
                    .findFirst()
                    .orElse(null);
            if (seleccion == null || !grupo.productoIdsPermitidos().contains(seleccion.productoId())) {
                throw new ComboSeleccionIncompletaException(grupo.nombre());
            }
        }

        PedidoItem item = new PedidoItem(pedido.getTenantId(), pedido.getId(), null, comboId, null, cantidad,
                observacion, combo.precio());
        item = pedidoItemRepository.guardar(item);
        for (SeleccionCombo seleccion : seleccionesSeguras) {
            pedidoItemRepository.guardarSeleccion(new PedidoItemComboSeleccion(pedido.getTenantId(), item.getId(),
                    seleccion.comboGrupoId(), seleccion.productoId()));
        }
        return item;
    }

    private PedidoDetalle construirDetalle(Pedido pedido) {
        MesaResumen mesa = pedido.getMesaId() == null ? null
                : zonaService.buscarMesaResumenPorId(pedido.getMesaId()).orElseThrow(MesaNoEncontradaException::new);
        Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId()).orElseThrow();

        List<PedidoItem> itemEntities = pedidoItemRepository.listarDePedido(pedido.getId());
        List<PedidoItemDetalle> items = itemEntities.stream().map(this::aDetalle).toList();
        BigDecimal subtotal = items.stream().map(PedidoItemDetalle::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ItemParaPromocion> itemsParaPromocion = itemEntities.stream()
                .map(i -> new ItemParaPromocion(i.getProductoId(), i.getCantidad(), i.subtotal()))
                .toList();
        List<PromocionSugerida> promociones = promocionService.evaluarSugeridas(itemsParaPromocion, subtotal);

        return new PedidoDetalle(pedido.getId(), pedido.getNumeroOrden(), mesa, usuario, pedido.getEstado(), items,
                subtotal, promociones, pedido.getFechaApertura());
    }

    private PedidoItemDetalle aDetalle(PedidoItem item) {
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
        return new PedidoItemDetalle(item.getId(), item.getProductoId(), item.getComboId(), nombre,
                item.getCantidad(), item.getPrecioUnitario(), item.getObservacion(), item.getEstadoPreparacion(),
                item.subtotal());
    }

    private BigDecimal recalcularSubtotal(Integer pedidoId) {
        return pedidoItemRepository.listarDePedido(pedidoId).stream()
                .map(PedidoItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Pedido buscarPedido(Integer id) {
        return pedidoRepository.buscarPorId(id).orElseThrow(PedidoNoEncontradoException::new);
    }

    private PedidoItem buscarItemDePedido(Integer pedidoId, Integer itemId) {
        PedidoItem item = pedidoItemRepository.buscarPorId(itemId).orElseThrow(PedidoItemNoEncontradoException::new);
        if (!item.getPedidoId().equals(pedidoId)) {
            throw new PedidoItemNoEncontradoException();
        }
        return item;
    }
}
