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
import java.math.RoundingMode;
import java.util.ArrayList;
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
    private static final BigDecimal CIEN = new BigDecimal("100");

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
            log.warn("Factura {} - tenant sin credenciales Factus o numbering_range_id completos, se deja sin transmitir",
                    facturaDianId);
            return new ResultadoTransmisionFactus(false, null, null, null, false,
                    "Sin credenciales Factus (o rango de numeracion) configurados");
        }
        CredencialesFactus credenciales = credencialesOpt.get();

        Venta venta = ventaRepository.buscarPorId(factura.getVentaId()).orElseThrow(VentaNoEncontradaException::new);
        SolicitudTransmisionFactus solicitud = construirSolicitud(venta);

        ResultadoTransmisionFactus resultado = transmisorPort.transmitir(solicitud, credenciales.clientId(),
                credenciales.clientSecret(), credenciales.username(), credenciales.password(),
                credenciales.ambiente(), credenciales.numberingRangeId());

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
            // Persistir el motivo real, no solo dejarlo en el log del servidor
            // — quien opera la caja necesita poder ver POR QUE fallo (ver
            // INTEGRACION.md hallazgo 3.48). La factura queda 'pendiente',
            // asi que se puede reintentar el envio desde Caja -> Facturacion.
            FacturaDian conFallo = facturaDianRepository.buscarPorId(facturaDianId)
                    .orElseThrow(FacturaNoEncontradaException::new);
            conFallo.registrarFalloTransmision(resultado.mensajeError());
            facturaDianRepository.guardar(conFallo);
            notificacionesWebSocketService.facturaActualizada(conFallo.getTenantId(), facturaDianId);
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

        // La propina voluntaria no es base gravable ante la DIAN - se cobra
        // en el POS pero nunca se factura (ver DECISIONES YA TOMADAS). El
        // monto real a facturar es venta.total SIN la propina; los pagos
        // reportados a Factus se escalan proporcionalmente para que la suma
        // cuadre exacto con eso (hallazgo real: Factus rechazaba con 422
        // "La suma de todos los detalles de pago no es igual al total de la
        // factura" en cuanto una venta tenia propina).
        BigDecimal montoAFacturar = venta.getTotal().subtract(venta.getPropina());
        List<PagoTransmisionFactus> pagosFactus = escalarPagosSinPropina(pagos, venta.getTotal(), montoAFacturar);
        BigDecimal descuentoRatePercent = calcularDescuentoRatePercent(venta);

        return new SolicitudTransmisionFactus(venta.getCodigo(), clienteFactus, items, pagosFactus,
                descuentoRatePercent);
    }

    /**
     * Mismo porcentaje de descuento prorateado uniforme que ya usa
     * VentaService para calcular impuestos (ver su Javadoc) - antes esto no
     * se transmitia a Factus en absoluto (discountRate quedaba fijo en 0),
     * asi que cualquier venta con descuento real tambien terminaba con el
     * mismo 422 de suma de pagos. subtotal=0 (venta sin items, caso raro) no
     * divide por cero: no hay descuento que prorratear.
     */
    private BigDecimal calcularDescuentoRatePercent(Venta venta) {
        if (venta.getSubtotal().signum() == 0) {
            return BigDecimal.ZERO;
        }
        return venta.getDescuentoTotal().divide(venta.getSubtotal(), 8, RoundingMode.HALF_UP)
                .multiply(CIEN);
    }

    /**
     * Reparte montoAFacturar (total SIN propina) entre los metodos de pago
     * reales, en la misma proporcion en que se pagaron - restar la propina
     * de un solo metodo especifico favorecería/perjudicaría ese metodo en
     * los reportes de Factus sin motivo. El ultimo pago absorbe el
     * redondeo de centavos para que la suma final cuadre exacto (Factus
     * valida la suma al centavo, ver hallazgo real del 422).
     */
    private List<PagoTransmisionFactus> escalarPagosSinPropina(List<VentaPago> pagos, BigDecimal totalConPropina,
                                                                 BigDecimal montoAFacturar) {
        if (pagos.isEmpty() || totalConPropina.signum() == 0) {
            return List.of();
        }
        BigDecimal factor = montoAFacturar.divide(totalConPropina, 8, RoundingMode.HALF_UP);
        List<PagoTransmisionFactus> resultado = new ArrayList<>(pagos.size());
        BigDecimal acumulado = BigDecimal.ZERO;
        for (int i = 0; i < pagos.size(); i++) {
            VentaPago pago = pagos.get(i);
            String codigoFactus = codigoFactusDe(pago);
            BigDecimal monto;
            if (i == pagos.size() - 1) {
                monto = montoAFacturar.subtract(acumulado).setScale(2, RoundingMode.HALF_UP);
            } else {
                monto = pago.getMonto().multiply(factor).setScale(2, RoundingMode.HALF_UP);
                acumulado = acumulado.add(monto);
            }
            resultado.add(new PagoTransmisionFactus(codigoFactus, monto));
        }
        return resultado;
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

    private String codigoFactusDe(VentaPago pago) {
        MetodoPagoResumen metodoPago = metodoPagoService.buscarResumenPorId(pago.getMetodoPagoId()).orElse(null);
        String codigoFactus = metodoPago != null ? metodoPago.codigoFactus() : null;
        if (codigoFactus == null) {
            log.warn("metodo_pago_id={} sin codigo_factus configurado - usando fallback generico '{}'",
                    pago.getMetodoPagoId(), PAYMENT_METHOD_CODE_FALLBACK);
            codigoFactus = PAYMENT_METHOD_CODE_FALLBACK;
        }
        return codigoFactus;
    }
}
