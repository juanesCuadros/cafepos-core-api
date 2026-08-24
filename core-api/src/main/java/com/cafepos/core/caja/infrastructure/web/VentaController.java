package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.HistorialVentasService;
import com.cafepos.core.caja.application.PagoInput;
import com.cafepos.core.caja.application.PermisoRequerido;
import com.cafepos.core.caja.application.PromocionAplicadaInput;
import com.cafepos.core.caja.application.VentaService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import com.cafepos.core.shared.seguridad.PinStepUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * POST /ventas y POST /ventas/{id}/finalizar-entrega usan permiso DINAMICO
 * (caja.pos vs caja.venta_rapida segun pedido.tipo, ver VentaService) — se
 * chequea a mano con PermissionEvaluator inyectado, nunca con @PreAuthorize
 * estatico (ver conversacion "Modulo Caja" Parte 4). El resto de rutas
 * (historial, reimprimir, anular) SI usa @PreAuthorize normal.
 */
@RestController
@RequestMapping("/ventas")
@Tag(name = ApiTags.CAJA)
public class VentaController {

    private static final String MODULO_HISTORIAL = "caja.historial_ventas";
    private static final String ACCION_ANULAR = "anular";
    private static final String RECURSO_TIPO_VENTA = "venta";

    private final VentaService ventaService;
    private final HistorialVentasService historialVentasService;
    private final PermissionEvaluator permissionEvaluator;
    private final PinStepUpService pinStepUpService;

    public VentaController(VentaService ventaService, HistorialVentasService historialVentasService,
                            PermissionEvaluator permissionEvaluator, PinStepUpService pinStepUpService) {
        this.ventaService = ventaService;
        this.historialVentasService = historialVentasService;
        this.permissionEvaluator = permissionEvaluator;
        this.pinStepUpService = pinStepUpService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cobra un pedido (de mesa o de venta rapida) — endpoint central del modulo",
            description = "Permiso dinamico segun pedido.tipo: 'mesa' exige caja.pos:cobrar, 'venta_rapida' exige "
                    + "caja.venta_rapida:cobrar (chequeado a mano, no con @PreAuthorize).")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Caja cerrada, o suma de pagos no coincide con el total"),
            @ApiResponse(responseCode = "403", description = "Sin el permiso especifico segun tipo de pedido"),
            @ApiResponse(responseCode = "404", description = "Pedido, cliente, promocion o metodo de pago no encontrado"),
            @ApiResponse(responseCode = "409", description = "El pedido ya fue cobrado")
    })
    public VentaCobradaResponse cobrar(@Valid @RequestBody CobrarRequest request, Authentication authentication) {
        exigirPermiso(ventaService.determinarPermisoParaCobrar(request.pedidoId()), authentication);

        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        List<PromocionAplicadaInput> promociones = request.promocionesAplicadas() == null ? null
                : request.promocionesAplicadas().stream()
                        .map(p -> new PromocionAplicadaInput(p.promocionId(), p.montoDescuento()))
                        .toList();
        List<PagoInput> pagos = request.pagos() == null ? null
                : request.pagos().stream().map(p -> new PagoInput(p.metodoPagoId(), p.monto())).toList();

        return VentaCobradaResponse.de(ventaService.cobrar(request.pedidoId(), request.clienteId(),
                request.propina(), request.descuentoTotal(), promociones, pagos, principal.usuarioId()));
    }

    @PostMapping("/{id}/finalizar-entrega")
    @Operation(summary = "Marca la entrega del comprobante y libera la mesa si el pedido era de mesa",
            description = "Mismo criterio de permiso dinamico que POST /ventas, segun el pedido asociado a esta venta.")
    public FinalizarEntregaResponse finalizarEntrega(@PathVariable Integer id,
                                                      @RequestBody(required = false) FinalizarEntregaRequest request,
                                                      Authentication authentication) {
        exigirPermiso(ventaService.determinarPermisoParaFinalizarEntrega(id), authentication);
        return FinalizarEntregaResponse.de(ventaService.finalizarEntrega(id));
    }

    @GetMapping
    @PreAuthorize("hasPermission('caja.historial_ventas', 'ver')")
    @Operation(summary = "Historial de ventas, con filtros opcionales")
    public VentasHistorialResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                           @RequestParam(required = false) LocalDate fechaFin,
                                           @RequestParam(required = false) Integer metodoPagoId,
                                           @RequestParam(required = false) String estado,
                                           @RequestParam(required = false) Integer cajeroId) {
        return VentasHistorialResponse.de(
                historialVentasService.listar(fechaInicio, fechaFin, metodoPagoId, estado, cajeroId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('caja.historial_ventas', 'ver')")
    @Operation(summary = "Detalle completo de una venta: items, pagos, factura si tiene")
    public VentaDetalleResponse obtener(@PathVariable Integer id) {
        return VentaDetalleResponse.de(historialVentasService.detalle(id));
    }

    @PostMapping("/{id}/reimprimir")
    @PreAuthorize("hasPermission('caja.historial_ventas', 'reimprimir')")
    @Operation(summary = "Reimprime el comprobante de una venta (sin logica real de impresora)")
    public ReimprimirResponse reimprimir(@PathVariable Integer id) {
        historialVentasService.reimprimir(id);
        return ReimprimirResponse.ENVIADO;
    }

    @PostMapping("/{id}/anular")
    @PreAuthorize("hasPermission('caja.historial_ventas', 'anular')")
    @Operation(summary = "Anula una venta",
            description = "Requiere PIN de step-up — header X-Pin-Token con el pin_token emitido por "
                    + "POST /auth/pin/verificar para modulo=caja.historial_ventas, accion=anular, "
                    + "recurso_tipo=venta, recurso_id=el id de esta venta. Genera nota_credito si la venta "
                    + "tenia factura DIAN asociada.")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es valido")
    })
    public AnularVentaResponse anular(@PathVariable Integer id, @Valid @RequestBody AnularVentaRequest request,
                                       @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                       @RequestHeader(name = "X-Pin-Token", required = false) String pinToken) {
        pinStepUpService.validar(pinToken, MODULO_HISTORIAL, ACCION_ANULAR, RECURSO_TIPO_VENTA, id);
        return AnularVentaResponse.de(historialVentasService.anular(id, request.motivo()));
    }

    private void exigirPermiso(PermisoRequerido permiso, Authentication authentication) {
        if (!permissionEvaluator.hasPermission(authentication, permiso.modulo(), permiso.accion())) {
            throw new AccessDeniedException("No tienes el permiso '" + permiso.accion() + "' para '"
                    + permiso.modulo() + "'");
        }
    }
}
