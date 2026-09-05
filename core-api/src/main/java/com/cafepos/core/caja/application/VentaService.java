package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.caja.domain.CajaJornadaRepository;
import com.cafepos.core.caja.domain.ClienteNoEncontradoException;
import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.JornadaNoAbiertaException;
import com.cafepos.core.caja.domain.MetodoPagoNoEncontradoException;
import com.cafepos.core.caja.domain.PagoNoCoincideException;
import com.cafepos.core.caja.domain.PedidoNoEncontradoException;
import com.cafepos.core.caja.domain.PedidoYaCerradoException;
import com.cafepos.core.caja.domain.PromocionNoEncontradaException;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaPago;
import com.cafepos.core.caja.domain.VentaPagoRepository;
import com.cafepos.core.caja.domain.VentaPromocion;
import com.cafepos.core.caja.domain.VentaPromocionRepository;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteRef;
import com.cafepos.core.configuracion.application.ConfiguracionSistemaService;
import com.cafepos.core.operacion.application.PedidoService;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.operacion.domain.PedidoParaVenta;
import com.cafepos.core.productosmenu.application.PromocionService;
import com.cafepos.core.restaurante.application.FacturacionDianService;
import com.cafepos.core.restaurante.application.MetodoPagoService;
import com.cafepos.core.restaurante.domain.NumeroFacturaReservado;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.impuestos.ResolverTasaImpuesto;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Endpoint central del modulo (Parte 4 y 5). Llama directamente (sincrono,
 * misma transaccion) a operacion.PedidoService, productosmenu.PromocionService,
 * restaurante.{MetodoPagoService,FacturacionDianService}, clientes.ClienteService
 * y configuracion.ConfiguracionSistemaService — todos NamedInterface.
 */
@Service
public class VentaService {

    private static final String MODULO_POS = "caja.pos";
    private static final String MODULO_VENTA_RAPIDA = "caja.venta_rapida";
    private static final String ACCION_COBRAR = "cobrar";

    private static final String PREFIJO_CODIGO_VENTA = "VTA";
    private static final int PADDING_FACTURA = 6;

    private static final BigDecimal CIEN = new BigDecimal("100");
    /** Tolerancia de redondeo aceptada entre suma de pagos y total calculado (1 centavo). */
    private static final BigDecimal TOLERANCIA_PAGOS = new BigDecimal("0.01");

    private final VentaRepository ventaRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final VentaPromocionRepository ventaPromocionRepository;
    private final FacturaDianRepository facturaDianRepository;
    private final CajaJornadaRepository cajaJornadaRepository;
    private final PedidoService pedidoService;
    private final PromocionService promocionService;
    private final MetodoPagoService metodoPagoService;
    private final ClienteService clienteService;
    private final FacturacionDianService facturacionDianService;
    private final ConfiguracionSistemaService configuracionSistemaService;
    private final FacturaDianTransmisionService facturaDianTransmisionService;

    public VentaService(VentaRepository ventaRepository, VentaPagoRepository ventaPagoRepository,
                         VentaPromocionRepository ventaPromocionRepository,
                         FacturaDianRepository facturaDianRepository, CajaJornadaRepository cajaJornadaRepository,
                         PedidoService pedidoService, PromocionService promocionService,
                         MetodoPagoService metodoPagoService, ClienteService clienteService,
                         FacturacionDianService facturacionDianService,
                         ConfiguracionSistemaService configuracionSistemaService,
                         FacturaDianTransmisionService facturaDianTransmisionService) {
        this.ventaRepository = ventaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.ventaPromocionRepository = ventaPromocionRepository;
        this.facturaDianRepository = facturaDianRepository;
        this.cajaJornadaRepository = cajaJornadaRepository;
        this.pedidoService = pedidoService;
        this.promocionService = promocionService;
        this.metodoPagoService = metodoPagoService;
        this.clienteService = clienteService;
        this.facturacionDianService = facturacionDianService;
        this.configuracionSistemaService = configuracionSistemaService;
        this.facturaDianTransmisionService = facturaDianTransmisionService;
    }

    /** Usado por el controller ANTES de ejecutar cobrar() — chequeo de permiso dinamico, ver PermisoRequerido. */
    @Transactional(readOnly = true)
    public PermisoRequerido determinarPermisoParaCobrar(Integer pedidoId) {
        PedidoParaVenta pedido = pedidoService.buscarParaVenta(pedidoId).orElseThrow(PedidoNoEncontradoException::new);
        return permisoDe(pedido);
    }

    @Transactional(readOnly = true)
    public PermisoRequerido determinarPermisoParaFinalizarEntrega(Integer ventaId) {
        Venta venta = buscarVenta(ventaId);
        PedidoParaVenta pedido = pedidoService.buscarParaVenta(venta.getPedidoId())
                .orElseThrow(PedidoNoEncontradoException::new);
        return permisoDe(pedido);
    }

    private PermisoRequerido permisoDe(PedidoParaVenta pedido) {
        return new PermisoRequerido(pedido.esMesa() ? MODULO_POS : MODULO_VENTA_RAPIDA, ACCION_COBRAR);
    }

    /**
     * Validaciones en el orden dado en la conversacion "Modulo Caja" Parte 4:
     * 1. jornada abierta, 2. pedido existe y no cerrado (mas cliente_id
     * valido, extension razonable no listada explicitamente pero necesaria),
     * 3. promociones activas, 4. metodos de pago existentes, 5. suma de
     * pagos == total (con tolerancia de 1 centavo).
     */
    @Transactional
    public VentaResultado cobrar(Integer pedidoId, Integer clienteId, BigDecimal propina, BigDecimal descuentoManual,
                                  List<PromocionAplicadaInput> promocionesAplicadas, List<PagoInput> pagos,
                                  Integer cajeroId) {
        List<PromocionAplicadaInput> promocionesSeguras = promocionesAplicadas == null ? List.of() : promocionesAplicadas;
        List<PagoInput> pagosSeguros = pagos == null ? List.of() : pagos;

        CajaJornada jornada = cajaJornadaRepository.buscarAbierta().orElseThrow(JornadaNoAbiertaException::new);

        PedidoParaVenta pedido = pedidoService.buscarParaVenta(pedidoId).orElseThrow(PedidoNoEncontradoException::new);
        if (pedido.estaCerrado()) {
            throw new PedidoYaCerradoException();
        }

        ClienteRef cliente = null;
        if (clienteId != null) {
            cliente = clienteService.buscarParaVenta(clienteId).orElseThrow(ClienteNoEncontradoException::new);
        }

        for (PromocionAplicadaInput p : promocionesSeguras) {
            if (!promocionService.existeActiva(p.promocionId())) {
                throw new PromocionNoEncontradaException();
            }
        }

        for (PagoInput p : pagosSeguros) {
            if (metodoPagoService.buscarResumenPorId(p.metodoPagoId()).isEmpty()) {
                throw new MetodoPagoNoEncontradoException();
            }
        }

        BigDecimal incPorcentajeDefault = configuracionSistemaService.obtenerIncPorcentaje();
        BigDecimal subtotalRaw = BigDecimal.ZERO;
        BigDecimal impuestosSinDescuento = BigDecimal.ZERO;
        for (PedidoItemParaVenta item : pedido.items()) {
            BigDecimal itemSubtotal = item.precioUnitario().multiply(item.cantidad());
            subtotalRaw = subtotalRaw.add(itemSubtotal);
            BigDecimal tasa = ResolverTasaImpuesto.tasa(item.tasaImpuesto(), incPorcentajeDefault);
            impuestosSinDescuento = impuestosSinDescuento.add(
                    itemSubtotal.multiply(tasa).divide(CIEN, 6, RoundingMode.HALF_UP));
        }
        BigDecimal subtotal = subtotalRaw.setScale(2, RoundingMode.HALF_UP);

        BigDecimal descuentoManualSeguro = descuentoManual == null ? BigDecimal.ZERO : descuentoManual;
        BigDecimal descuentoPromos = promocionesSeguras.stream()
                .map(PromocionAplicadaInput::montoDescuento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descuentoTotal = descuentoManualSeguro.add(descuentoPromos).setScale(2, RoundingMode.HALF_UP);

        /**
         * impuestos: suma por item de (precio_unitario*cantidad) * tasa_item,
         * escalado proporcionalmente por (subtotal - descuento_total)/subtotal.
         * Matematicamente equivalente a prorratear el descuento item por
         * item ANTES de aplicar la tasa (mismo factor de reduccion aplicado
         * de forma uniforme) — no hace falta la simplificacion global que
         * el prompt permitia, el descuento de todas formas no viene
         * desglosado por item en el request (promociones_aplicadas trae
         * solo un monto total), asi que prorratear uniformemente es la
         * unica opcion consistente con los datos disponibles.
         */
        BigDecimal impuestos;
        if (subtotal.signum() == 0) {
            impuestos = BigDecimal.ZERO;
        } else {
            BigDecimal factor = subtotal.subtract(descuentoTotal).divide(subtotal, 8, RoundingMode.HALF_UP);
            if (factor.signum() < 0) {
                factor = BigDecimal.ZERO;
            }
            impuestos = impuestosSinDescuento.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal propinaSegura = (propina == null ? BigDecimal.ZERO : propina).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(descuentoTotal).add(impuestos).add(propinaSegura)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal sumaPagos = pagosSeguros.stream().map(PagoInput::monto).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaPagos.subtract(total).abs().compareTo(TOLERANCIA_PAGOS) > 0) {
            throw new PagoNoCoincideException();
        }

        Integer tenantId = TenantContext.getCurrentTenantId();
        Venta venta = new Venta(tenantId, pedidoId, jornada.getId(), clienteId, cajeroId, subtotal, descuentoTotal,
                impuestos, propinaSegura, total);
        // El chequeo de pedido.estaCerrado() de arriba es "check-then-act"
        // sin bloqueo — dos cobros casi simultaneos del mismo pedido pueden
        // pasarlo los dos antes de que ninguno confirme su venta (mismo
        // problema real ya resuelto para mesas y turno, ver
        // V29__pedido_mesa_activo_unico.sql / V31__turno_activo_unico.sql).
        // La garantia real es el indice unico de V32__venta_pedido_unico.sql
        // sobre venta.pedido_id — este chequeo previo sigue sirviendo para
        // el caso normal (dar un error legible sin ni siquiera intentar el
        // INSERT), pero si igual se pierde la carrera, el INSERT choca
        // contra el indice y Postgres tira DataIntegrityViolationException
        // — se traduce al mismo error legible (reusando PedidoYaCerradoException,
        // mismo significado exacto: "este pedido ya fue cobrado") en vez de
        // dejarlo escapar como 500 generico.
        try {
            venta = ventaRepository.guardar(venta);
        } catch (DataIntegrityViolationException ex) {
            throw new PedidoYaCerradoException();
        }
        venta.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO_VENTA, venta.getId()));
        venta = ventaRepository.guardar(venta);

        for (PagoInput p : pagosSeguros) {
            ventaPagoRepository.guardar(new VentaPago(tenantId, venta.getId(), p.metodoPagoId(), p.monto()));
        }
        for (PromocionAplicadaInput p : promocionesSeguras) {
            ventaPromocionRepository.guardar(
                    new VentaPromocion(tenantId, venta.getId(), p.promocionId(), p.montoDescuento()));
        }

        FacturaResumen facturaResumen = null;
        if (clienteId != null) {
            facturaResumen = emitirFacturaSiCorresponde(tenantId, venta.getId());
        }

        pedidoService.marcarCerrado(pedidoId);

        return new VentaResultado(venta, cliente, facturaResumen);
    }

    /**
     * null si el tenant no tiene ninguna resolucion DIAN configurada —
     * cliente queda asociado igual, sin factura. El prefijo y el
     * resolucion_id son los REALES de esa resolucion especifica (nunca un
     * "FE" hardcodeado aca — distintos tenants pueden tener prefijos
     * distintos asignados por la DIAN). Si SI se crea la factura, programa
     * el intento real de transmision a Factus para DESPUES de que esta
     * transaccion confirme (ver FacturaDianTransmisionService.programarTransmisionTrasCommit)
     * — nunca antes, y nunca bloqueando la respuesta de este metodo.
     */
    private FacturaResumen emitirFacturaSiCorresponde(Integer tenantId, Integer ventaId) {
        Optional<NumeroFacturaReservado> reserva = facturacionDianService.reservarSiguienteNumeroFactura();
        if (reserva.isEmpty()) {
            return null;
        }
        NumeroFacturaReservado r = reserva.get();
        String numeroFactura = GeneradorCodigo.generar(r.prefijo(), r.numero(), PADDING_FACTURA);
        FacturaDian factura = new FacturaDian(tenantId, ventaId, r.resolucionId(), numeroFactura);
        factura = facturaDianRepository.guardar(factura);
        facturaDianTransmisionService.programarTransmisionTrasCommit(factura.getId(), tenantId);
        return new FacturaResumen(factura.getId(), factura.getNumeroFactura(), factura.getEstadoDian());
    }

    @Transactional
    public boolean finalizarEntrega(Integer ventaId) {
        Venta venta = buscarVenta(ventaId);
        return pedidoService.finalizarEntrega(venta.getPedidoId());
    }

    private Venta buscarVenta(Integer id) {
        return ventaRepository.buscarPorId(id).orElseThrow(VentaNoEncontradaException::new);
    }
}
