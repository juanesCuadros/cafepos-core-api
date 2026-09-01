package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.ClienteNoEncontradoException;
import com.cafepos.core.caja.domain.ClienteTransmisionFactus;
import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.FacturaDianTransmisorPort;
import com.cafepos.core.caja.domain.FacturaNoEncontradaException;
import com.cafepos.core.caja.domain.ItemTransmisionFactus;
import com.cafepos.core.caja.domain.PagoTransmisionFactus;
import com.cafepos.core.caja.domain.PedidoNoEncontradoException;
import com.cafepos.core.caja.domain.ResultadoTransmisionFactus;
import com.cafepos.core.caja.domain.SolicitudTransmisionFactus;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaPago;
import com.cafepos.core.caja.domain.VentaPagoRepository;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteParaFacturaDian;
import com.cafepos.core.configuracion.application.ConfiguracionSistemaService;
import com.cafepos.core.operacion.application.PedidoService;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.operacion.domain.PedidoParaVenta;
import com.cafepos.core.restaurante.application.FacturacionDianService;
import com.cafepos.core.restaurante.application.MetodoPagoService;
import com.cafepos.core.restaurante.domain.CredencialesFactus;
import com.cafepos.core.restaurante.domain.MetodoPagoResumen;
import com.cafepos.core.shared.impuestos.ResolverTasaImpuesto;
import com.cafepos.core.shared.tenant.TenantContext;
import com.cafepos.core.shared.websocket.NotificacionesWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Orquesta el intento REAL de transmision de una factura DIAN a Factus —
 * unico caller de FacturaDianTransmisorPort (com.cafepos.core.caja.infrastructure.factus).
 * Dos entradas:
 *   - programarTransmisionTrasCommit: llamada DENTRO de la transaccion de
 *     VentaService.cobrar (antes de su commit) — registra el intento real
 *     para que corra en un hilo aparte DESPUES del commit, sin bloquear la
 *     respuesta de POST /ventas.
 *   - transmitir: llamada SINCRONICAMENTE por FacturacionService.reintentarEnvio
 *     (el usuario espera ver el resultado de un reintento manual).
 * Ninguna de las dos propaga excepciones — un fallo de Factus (auth,
 * timeout, red, rechazo) deja factura_dian tal cual estaba y solo genera
 * un WARN, nunca un 500 ni afecta el 201 de la venta (ver DECISIONES YA
 * TOMADAS).
 *
 * No usa @Transactional en ningun metodo propio a proposito: cada llamada
 * a un repositorio TenantAwareRepository o a un service NamedInterface de
 * otro modulo ya es transaccional por si sola (ver CLAUDE.md) — envolver
 * todo el flujo en una sola transaccion mantendria una conexion abierta
 * durante la llamada de red a Factus (hasta 15 segundos), que es
 * exactamente lo que se quiere evitar.
 */
@Service
public class FacturaDianTransmisionService {

    private static final String TIPO_DOC_NIT = "NIT";
    private static final String DOC_CODE_CC = "13";
    private static final String DOC_CODE_NIT = "31";
    private static final String LEGAL_ORG_NATURAL = "2";
    private static final String LEGAL_ORG_JURIDICA = "1";
    private static final String TAX_CODE_IVA = "01";
    private static final String TAX_CODE_INC = "04";
    private static final String PAYMENT_METHOD_CODE_FALLBACK = "42";

    private static final Logger log = LoggerFactory.getLogger(FacturaDianTransmisionService.class);

    private final FacturaDianRepository facturaDianRepository;
    private final VentaRepository ventaRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final MetodoPagoService metodoPagoService;
    private final FacturacionDianService facturacionDianService;
    private final ConfiguracionSistemaService configuracionSistemaService;
    private final FacturaDianTransmisorPort transmisorPort;
    private final ExecutorService facturaDianTransmisionExecutor;
    private final NotificacionesWebSocketService notificacionesWebSocketService;

    public FacturaDianTransmisionService(FacturaDianRepository facturaDianRepository, VentaRepository ventaRepository,
                                          VentaPagoRepository ventaPagoRepository, PedidoService pedidoService,
                                          ClienteService clienteService, MetodoPagoService metodoPagoService,
                                          FacturacionDianService facturacionDianService,
                                          ConfiguracionSistemaService configuracionSistemaService,
                                          FacturaDianTransmisorPort transmisorPort,
                                          ExecutorService facturaDianTransmisionExecutor,
                                          NotificacionesWebSocketService notificacionesWebSocketService) {
        this.facturaDianRepository = facturaDianRepository;
        this.ventaRepository = ventaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.metodoPagoService = metodoPagoService;
        this.facturacionDianService = facturacionDianService;
        this.configuracionSistemaService = configuracionSistemaService;
        this.transmisorPort = transmisorPort;
        this.facturaDianTransmisionExecutor = facturaDianTransmisionExecutor;
        this.notificacionesWebSocketService = notificacionesWebSocketService;
    }

    public void programarTransmisionTrasCommit(Integer facturaDianId, Integer tenantId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                facturaDianTransmisionExecutor.execute(() -> transmitirEnHiloNuevo(facturaDianId, tenantId));
            }
        });
    }

    private void transmitirEnHiloNuevo(Integer facturaDianId, Integer tenantId) {
        TenantContext.setCurrentTenantId(tenantId);
        try {
            transmitir(facturaDianId);
        } finally {
            TenantContext.clear();
        }
    }

    public ResultadoTransmisionFactus transmitir(Integer facturaDianId) {
        try {
            return intentar(facturaDianId);
        } catch (RuntimeException ex) {
            log.warn("No se pudo transmitir la factura {} a Factus - se deja el estado actual: {}", facturaDianId,
                    ex.getMessage());
            return new ResultadoTransmisionFactus(false, null, null, null, false,
                    "Error interno intentando transmitir a Factus");
        }
    }

    private ResultadoTransmisionFactus intentar(Integer facturaDianId) {
        FacturaDian factura = facturaDianRepository.buscarPorId(facturaDianId)
                .orElseThrow(FacturaNoEncontradaException::new);

        Optional<CredencialesFactus> credencialesOpt = facturacionDianService.credencialesFactusPara();
        if (credencialesOpt.isEmpty()) {
            log.warn("Factura {} - tenant sin credenciales Factus completas configuradas, se deja sin transmitir",
                    facturaDianId);
            return new ResultadoTransmisionFactus(false, null, null, null, false,
                    "Sin credenciales Factus configuradas");
        }
        CredencialesFactus credenciales = credencialesOpt.get();

        Venta venta = ventaRepository.buscarPorId(factura.getVentaId()).orElseThrow(VentaNoEncontradaException::new);
        SolicitudTransmisionFactus solicitud = construirSolicitud(venta);

        ResultadoTransmisionFactus resultado = transmisorPort.transmitir(solicitud, credenciales.clientId(),
                credenciales.clientSecret(), credenciales.username(), credenciales.password(),
                credenciales.ambiente());

        if (resultado.exitoso()) {
            FacturaDian aActualizar = facturaDianRepository.buscarPorId(facturaDianId)
                    .orElseThrow(FacturaNoEncontradaException::new);
            aActualizar.actualizarConResultadoFactus(resultado.numeroFactura(), resultado.cufe(),
                    resultado.qrCode(), resultado.validado());
            facturaDianRepository.guardar(aActualizar);
            // Aviso al frontend de que ESTA factura ya tiene resultado real de
            // la DIAN — sin esto el canal WS existia en los dos lados pero
            // nadie lo disparaba nunca, y FacturaEspera caia a preguntar por
            // HTTP cada 3s (ver INTEGRACION.md hallazgo 3.43). El estado solo
            // cambia en esta rama: si la llamada a Factus falla (auth, red,
            // timeout) la factura queda 'pendiente' y no hay nada nuevo que
            // notificar.
            notificacionesWebSocketService.facturaActualizada(aActualizar.getTenantId(), facturaDianId);
        } else {
            log.warn("Factus no acepto la transmision de la factura {}: {}", facturaDianId, resultado.mensajeError());
        }
        return resultado;
    }

    private SolicitudTransmisionFactus construirSolicitud(Venta venta) {
        PedidoParaVenta pedido = pedidoService.buscarParaVenta(venta.getPedidoId())
                .orElseThrow(PedidoNoEncontradoException::new);
        ClienteParaFacturaDian cliente = clienteService.buscarParaFacturaDian(venta.getClienteId())
                .orElseThrow(ClienteNoEncontradoException::new);
        List<VentaPago> pagos = ventaPagoRepository.listarDeVenta(venta.getId());
        BigDecimal incPorcentajeDefault = configuracionSistemaService.obtenerIncPorcentaje();

        ClienteTransmisionFactus clienteFactus = mapearCliente(cliente);
        List<ItemTransmisionFactus> items = pedido.items().stream()
                .map(item -> mapearItem(item, incPorcentajeDefault))
                .toList();
        List<PagoTransmisionFactus> pagosFactus = pagos.stream().map(this::mapearPago).toList();

        return new SolicitudTransmisionFactus(venta.getCodigo(), clienteFactus, items, pagosFactus);
    }

    /** CC -> persona natural (legal_organization_code "2", va en names). NIT -> juridica ("1", va en company). */
    private ClienteTransmisionFactus mapearCliente(ClienteParaFacturaDian cliente) {
        boolean esJuridica = TIPO_DOC_NIT.equalsIgnoreCase(cliente.tipoDocumento());
        String documentCode = esJuridica ? DOC_CODE_NIT : DOC_CODE_CC;
        String legalOrgCode = esJuridica ? LEGAL_ORG_JURIDICA : LEGAL_ORG_NATURAL;
        String names = esJuridica ? null : cliente.nombre();
        String company = esJuridica ? cliente.nombre() : null;
        return new ClienteTransmisionFactus(documentCode, cliente.numeroDocumento(), legalOrgCode, names, company,
                cliente.correo());
    }

    private ItemTransmisionFactus mapearItem(PedidoItemParaVenta item, BigDecimal incPorcentajeDefault) {
        BigDecimal tasa = ResolverTasaImpuesto.tasa(item.tasaImpuesto(), incPorcentajeDefault);
        String taxCode = ResolverTasaImpuesto.esIva(item.tasaImpuesto()) ? TAX_CODE_IVA : TAX_CODE_INC;
        String codeReference = item.codigo() != null ? item.codigo() : String.valueOf(item.id());
        return new ItemTransmisionFactus(codeReference, item.nombre(), item.cantidad(), item.precioUnitario(),
                taxCode, tasa);
    }

    private PagoTransmisionFactus mapearPago(VentaPago pago) {
        MetodoPagoResumen metodoPago = metodoPagoService.buscarResumenPorId(pago.getMetodoPagoId()).orElse(null);
        String codigoFactus = metodoPago != null ? metodoPago.codigoFactus() : null;
        if (codigoFactus == null) {
            log.warn("metodo_pago_id={} sin codigo_factus configurado - usando fallback generico '{}'",
                    pago.getMetodoPagoId(), PAYMENT_METHOD_CODE_FALLBACK);
            codigoFactus = PAYMENT_METHOD_CODE_FALLBACK;
        }
        return new PagoTransmisionFactus(codigoFactus, pago.getMonto());
    }
}
