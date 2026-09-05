package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.EstadoFacturaInvalidoException;
import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import com.cafepos.core.caja.domain.FacturaListadoItem;
import com.cafepos.core.caja.domain.FacturaNoEncontradaException;
import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.caja.domain.NotaCreditoRepository;
import com.cafepos.core.caja.domain.NotaCreditoYaExisteException;
import com.cafepos.core.caja.domain.ResultadoEnvioCorreoFactus;
import com.cafepos.core.caja.domain.ResultadoTransmisionFactus;
import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.ClienteParaFactura;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Facturacion (api_03_caja.md 3.6). reenviar-correo y reintentar-envio
 * ejecutan ambos llamadas reales a Factus (ver FacturaDianTransmisionService)
 * — reenviar-correo ya no es un stub.
 */
@Service
public class FacturacionService {

    private static final Set<String> ESTADOS_REINTENTABLES = Set.of(FacturaDian.ESTADO_PENDIENTE,
            FacturaDian.ESTADO_RECHAZADA);

    private final FacturaDianRepository facturaDianRepository;
    private final VentaRepository ventaRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final ClienteService clienteService;
    private final FacturaDianTransmisionService facturaDianTransmisionService;

    public FacturacionService(FacturaDianRepository facturaDianRepository, VentaRepository ventaRepository,
                               NotaCreditoRepository notaCreditoRepository, ClienteService clienteService,
                               FacturaDianTransmisionService facturaDianTransmisionService) {
        this.facturaDianRepository = facturaDianRepository;
        this.ventaRepository = ventaRepository;
        this.notaCreditoRepository = notaCreditoRepository;
        this.clienteService = clienteService;
        this.facturaDianTransmisionService = facturaDianTransmisionService;
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
     * Reenvio REAL via Factus (POST /v2/bills/{numero}/send-email, ver
     * FacturaDianTransmisionService.enviarCorreoReal — reemplaza el stub que
     * solo logueaba, ver INTEGRACION.md hallazgo del mensaje que le mentia
     * al usuario). Sin @Transactional a proposito: enviarCorreoReal hace una
     * llamada de red a Factus (hasta 15s), no conviene mantener una conexion
     * de base abierta durante eso (mismo criterio que reintentarEnvio).
     * permiso real del catalogo es caja.facturacion:enviar_correo (el
     * contrato de este prompt decia "reenviar_correo", que no existe en el
     * catalogo).
     */
    public ReenviarCorreoResultado reenviarCorreo(Integer id) {
        FacturaDian factura = buscarFactura(id);
        Venta venta = buscarVenta(factura.getVentaId());
        ClienteParaFactura cliente = venta.getClienteId() == null ? null
                : clienteService.buscarParaFactura(venta.getClienteId()).orElse(null);
        String correo = cliente == null ? null : cliente.correo();

        if (correo == null || correo.isBlank()) {
            return new ReenviarCorreoResultado("La factura no tiene un correo de cliente registrado para reenviar");
        }

        ResultadoEnvioCorreoFactus resultado = facturaDianTransmisionService.enviarCorreoReal(id, correo);
        if (!resultado.exitoso()) {
            return new ReenviarCorreoResultado(resultado.mensajeError());
        }
        return new ReenviarCorreoResultado("Factura reenviada a " + correo);
    }

    /**
     * Reintento REAL — solo aplica si el estado actual es 'pendiente' o
     * 'rechazada' (400 en cualquier otro caso). El intento a Factus corre
     * SINCRONICAMENTE aca (a diferencia del intento automatico tras
     * POST /ventas): esta es una accion manual explicita del usuario, que
     * espera ver el resultado real en la respuesta. Un fallo de Factus
     * (credenciales, timeout, red, rechazo) NO tira 500 — el mensaje
     * refleja el resultado real y estado_dian queda como haya quedado (ver
     * FacturaDianTransmisionService.transmitir, nunca propaga excepciones).
     */
    public ReintentarEnvioResultado reintentarEnvio(Integer id) {
        FacturaDian factura = buscarFactura(id);
        if (!ESTADOS_REINTENTABLES.contains(factura.getEstadoDian())) {
            throw new EstadoFacturaInvalidoException();
        }
        ResultadoTransmisionFactus resultado = facturaDianTransmisionService.transmitir(id);
        FacturaDian actualizada = buscarFactura(id);
        String mensaje = mensajeDe(resultado);
        return new ReintentarEnvioResultado(actualizada.getId(), actualizada.getEstadoDian(), mensaje);
    }

    private String mensajeDe(ResultadoTransmisionFactus resultado) {
        if (!resultado.exitoso()) {
            return "No se pudo transmitir la factura a la DIAN: " + resultado.mensajeError();
        }
        return resultado.validado() ? "Factura aceptada por la DIAN"
                : "Factura transmitida pero rechazada por la DIAN";
    }

    /**
     * El chequeo de PIN corre ANTES de esto, en el controller (ver
     * PinStepUpService) — con el permiso caja.facturacion:generar_nota_credito
     * (el contrato de este prompt pedia 'anular', que no existe en el
     * catalogo para caja.facturacion; generar_nota_credito es la accion con
     * requiere_pin=true mas cercana semanticamente). nota_credito SIN
     * devolucion_id — anulacion directa de factura, distinta de una
     * devolucion (ver DevolucionService.solicitar).
     *
     * No habia ningun chequeo de si esta factura ya tenia una nota_credito
     * generada — el boton "Generar nota credito" del frontend tampoco
     * deshabilita nada, asi que se podia clickear varias veces sobre la
     * misma factura sin ninguna carrera ni llamada directa a la API (ver
     * FASE1_AUDITORIA_OPERACION_CAJA_PRODUCTOS.md 3.4.2).
     */
    @Transactional
    @Auditable(entidadTipo = "factura_dian", accion = "anular", entidadIdExpression = "#id")
    public AnularFacturaResultado anular(Integer id, String motivo) {
        FacturaDian factura = buscarFactura(id);
        if (notaCreditoRepository.existePorFacturaId(id)) {
            throw new NotaCreditoYaExisteException();
        }
        AuditoriaContext.registrarAntes(factura);
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
