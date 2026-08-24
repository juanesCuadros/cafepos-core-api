package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.EstadoFacturaInvalidoException;
import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.FacturaListadoItem;
import com.cafepos.core.caja.domain.FacturaNoEncontradaException;
import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.caja.domain.NotaCreditoRepository;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteParaFactura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Facturacion (api_03_caja.md 3.6). reenviar-correo y reintentar-envio son
 * stubs a proposito — sin proveedor de correo real ni transmision real a
 * Factus en esta version, ver los comentarios de cada metodo.
 */
@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);

    private static final Set<String> ESTADOS_REINTENTABLES = Set.of(FacturaDian.ESTADO_PENDIENTE,
            FacturaDian.ESTADO_RECHAZADA);

    private final FacturaDianRepository facturaDianRepository;
    private final VentaRepository ventaRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final ClienteService clienteService;

    public FacturacionService(FacturaDianRepository facturaDianRepository, VentaRepository ventaRepository,
                               NotaCreditoRepository notaCreditoRepository, ClienteService clienteService) {
        this.facturaDianRepository = facturaDianRepository;
        this.ventaRepository = ventaRepository;
        this.notaCreditoRepository = notaCreditoRepository;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    public List<FacturaListadoItem> listar(LocalDate fechaInicio, LocalDate fechaFin, String estadoDian,
                                            String cliente, String numeroFactura) {
        OffsetDateTime desde = fechaInicio == null ? null : fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = fechaFin == null ? null : fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return facturaDianRepository.listar(desde, hasta, estadoDian, cliente, numeroFactura);
    }

    @Transactional(readOnly = true)
    public FacturaDetalleVista detalle(Integer id) {
        FacturaDian factura = buscarFactura(id);
        Venta venta = buscarVenta(factura.getVentaId());
        ClienteParaFactura cliente = venta.getClienteId() == null ? null
                : clienteService.buscarParaFactura(venta.getClienteId()).orElse(null);
        return new FacturaDetalleVista(factura, venta.getTotal(), cliente);
    }

    /**
     * STUB: sin proveedor de correo real conectado (mismo criterio que la
     * recuperacion de contrasena diferida) — solo deja un log INFO del
     * intento, nunca envia nada de verdad. permiso real del catalogo es
     * caja.facturacion:enviar_correo (el contrato de este prompt decia
     * "reenviar_correo", que no existe en el catalogo).
     */
    @Transactional(readOnly = true)
    public ReenviarCorreoResultado reenviarCorreo(Integer id) {
        FacturaDian factura = buscarFactura(id);
        Venta venta = buscarVenta(factura.getVentaId());
        ClienteParaFactura cliente = venta.getClienteId() == null ? null
                : clienteService.buscarParaFactura(venta.getClienteId()).orElse(null);
        String correo = cliente == null ? null : cliente.correo();

        if (correo == null || correo.isBlank()) {
            log.info("[STUB reenviar-correo] Factura {} sin correo de cliente registrado, no se reenvia nada",
                    factura.getNumeroFactura());
            return new ReenviarCorreoResultado("La factura no tiene un correo de cliente registrado para reenviar");
        }

        log.info("[STUB reenviar-correo] Factura {} 'reenviada' a {} (sin proveedor de correo real conectado)",
                factura.getNumeroFactura(), correo);
        return new ReenviarCorreoResultado("Factura reenviada a " + correo);
    }

    /**
     * STUB: sin transmision real a Factus en esta version — no hay nada que
     * reintentar de verdad, estado_dian queda IGUAL. Solo aplica si el
     * estado actual es 'pendiente' o 'rechazada' (400 en cualquier otro caso).
     */
    @Transactional(readOnly = true)
    public ReintentarEnvioResultado reintentarEnvio(Integer id) {
        FacturaDian factura = buscarFactura(id);
        if (!ESTADOS_REINTENTABLES.contains(factura.getEstadoDian())) {
            throw new EstadoFacturaInvalidoException();
        }
        log.info("[STUB reintentar-envio] Factura {} — sin transmision real a Factus conectada",
                factura.getNumeroFactura());
        return new ReintentarEnvioResultado(factura.getId(), factura.getEstadoDian(),
                "Reintentando transmision a la DIAN");
    }

    /**
     * El chequeo de PIN corre ANTES de esto, en el controller (ver
     * PinStepUpService) — con el permiso caja.facturacion:generar_nota_credito
     * (el contrato de este prompt pedia 'anular', que no existe en el
     * catalogo para caja.facturacion; generar_nota_credito es la accion con
     * requiere_pin=true mas cercana semanticamente). nota_credito SIN
     * devolucion_id — anulacion directa de factura, distinta de una
     * devolucion (ver DevolucionService.solicitar).
     */
    @Transactional
    public AnularFacturaResultado anular(Integer id, String motivo) {
        FacturaDian factura = buscarFactura(id);
        Venta venta = buscarVenta(factura.getVentaId());
        NotaCredito notaCredito = new NotaCredito(factura.getTenantId(), factura.getId(), motivo, venta.getTotal());
        notaCredito = notaCreditoRepository.guardar(notaCredito);
        return new AnularFacturaResultado(notaCredito.getId(), factura.getId(), venta.getTotal());
    }

    private FacturaDian buscarFactura(Integer id) {
        return facturaDianRepository.buscarPorId(id).orElseThrow(FacturaNoEncontradaException::new);
    }

    private Venta buscarVenta(Integer id) {
        return ventaRepository.buscarPorId(id).orElseThrow(VentaNoEncontradaException::new);
    }
}
