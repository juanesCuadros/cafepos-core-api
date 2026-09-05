package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.caja.domain.NotaCreditoRepository;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaYaAnuladaException;
import com.cafepos.core.caja.domain.VentaPago;
import com.cafepos.core.caja.domain.VentaPagoRepository;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteRef;
import com.cafepos.core.operacion.application.PedidoService;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.restaurante.application.MetodoPagoService;
import com.cafepos.core.restaurante.domain.MetodoPagoResumen;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/** Historial de ventas — Parte 7 (Facturacion/Devoluciones quedan fuera de alcance). */
@Service
public class HistorialVentasService {

    private final VentaRepository ventaRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final FacturaDianRepository facturaDianRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final MetodoPagoService metodoPagoService;

    public HistorialVentasService(VentaRepository ventaRepository, VentaPagoRepository ventaPagoRepository,
                                   FacturaDianRepository facturaDianRepository,
                                   NotaCreditoRepository notaCreditoRepository, UsuarioRepository usuarioRepository,
                                   ClienteService clienteService, PedidoService pedidoService,
                                   MetodoPagoService metodoPagoService) {
        this.ventaRepository = ventaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.facturaDianRepository = facturaDianRepository;
        this.notaCreditoRepository = notaCreditoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.metodoPagoService = metodoPagoService;
    }

    @Transactional(readOnly = true)
    public List<VentaResumenVista> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer metodoPagoId,
                                           String estado, Integer cajeroId) {
        OffsetDateTime desde = fechaInicio == null ? null : fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = fechaFin == null ? null
                : fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return ventaRepository.listar(desde, hasta, metodoPagoId, estado, cajeroId).stream()
                .map(this::aResumen)
                .toList();
    }

    private VentaResumenVista aResumen(Venta venta) {
        String cajeroNombre = usuarioRepository.findById(venta.getCajeroId()).map(Usuario::getNombre).orElse(null);
        List<String> metodosPago = ventaPagoRepository.nombresMetodoPagoDeVenta(venta.getId());
        return new VentaResumenVista(venta, cajeroNombre, metodosPago);
    }

    @Transactional(readOnly = true)
    public VentaDetalleVista detalle(Integer id) {
        Venta venta = buscarVenta(id);
        String cajeroNombre = usuarioRepository.findById(venta.getCajeroId()).map(Usuario::getNombre).orElse(null);
        ClienteRef cliente = venta.getClienteId() == null ? null
                : clienteService.buscarParaVenta(venta.getClienteId()).orElse(null);
        List<PedidoItemParaVenta> items = pedidoService.buscarParaVenta(venta.getPedidoId())
                .map(p -> p.items())
                .orElse(List.of());
        List<VentaPagoDetalle> pagos = ventaPagoRepository.listarDeVenta(id).stream()
                .map(this::aPagoDetalle)
                .toList();
        FacturaResumen factura = facturaDianRepository.buscarPorVentaId(id)
                .map(f -> new FacturaResumen(f.getId(), f.getNumeroFactura(), f.getEstadoDian()))
                .orElse(null);
        return new VentaDetalleVista(venta, cajeroNombre, cliente, items, pagos, factura);
    }

    private VentaPagoDetalle aPagoDetalle(VentaPago pago) {
        String nombre = metodoPagoService.buscarResumenPorId(pago.getMetodoPagoId())
                .map(MetodoPagoResumen::nombre)
                .orElse(null);
        return new VentaPagoDetalle(nombre, pago.getMonto());
    }

    /** Sin logica real de impresora — fuera de alcance, ver DECISIONES YA TOMADAS. */
    @Transactional(readOnly = true)
    public void reimprimir(Integer id) {
        buscarVenta(id);
    }

    /**
     * El chequeo de PIN corre ANTES de esto, en el controller (ver
     * PinStepUpService). nota_credito SOLO si la venta tiene factura_dian
     * asociada, sin importar su estado_dian (nunca llega a "aceptada" en
     * esta version sin Factus real).
     *
     * Primer y unico caso instrumentado con @Auditable por ahora (prueba de
     * concepto, ver shared.auditoria) — registrarAntes(venta) tiene que
     * llamarse ANTES de venta.anular() para capturar el estado real previo.
     *
     * `estaCobrada()` existia en la entidad pero nadie lo llamaba — anular
     * una venta ya anulada no se rechazaba y generaba otra nota_credito mas
     * cada vez (ver FASE1_AUDITORIA_OPERACION_CAJA_PRODUCTOS.md 3.2.1).
     */
    @Transactional
    @Auditable(entidadTipo = "venta", accion = "anular", entidadIdExpression = "#ventaId")
    public AnulacionResultado anular(Integer ventaId, String motivo) {
        Venta venta = buscarVenta(ventaId);
        if (!venta.estaCobrada()) {
            throw new VentaYaAnuladaException();
        }
        AuditoriaContext.registrarAntes(venta);
        venta.anular();
        venta = ventaRepository.guardar(venta);

        Optional<FacturaDian> factura = facturaDianRepository.buscarPorVentaId(ventaId);
        if (factura.isEmpty()) {
            return new AnulacionResultado(venta, false, null);
        }
        NotaCredito notaCredito = new NotaCredito(venta.getTenantId(), factura.get().getId(), motivo,
                venta.getTotal());
        notaCredito = notaCreditoRepository.guardar(notaCredito);
        return new AnulacionResultado(venta, true, notaCredito.getId());
    }

    private Venta buscarVenta(Integer id) {
        return ventaRepository.buscarPorId(id).orElseThrow(VentaNoEncontradaException::new);
    }
}
