package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.FacturacionService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.PinStepUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * Facturacion (api_03_caja.md 3.6). El contrato pedia hasPermission(
 * 'caja.facturacion','anular') y 'reenviar_correo' — el catalogo real no
 * tiene esas acciones para caja.facturacion, tiene 'generar_nota_credito'
 * (con requiere_pin=true, la mas cercana a "anular con nota credito") y
 * 'enviar_correo' — se usan esas dos (ver FacturacionService).
 */
@RestController
@RequestMapping("/facturas")
@Tag(name = ApiTags.CAJA)
public class FacturacionController {

    private static final String MODULO = "caja.facturacion";
    private static final String ACCION_GENERAR_NOTA_CREDITO = "generar_nota_credito";
    private static final String RECURSO_TIPO_FACTURA = "factura_dian";

    private final FacturacionService facturacionService;
    private final PinStepUpService pinStepUpService;

    public FacturacionController(FacturacionService facturacionService, PinStepUpService pinStepUpService) {
        this.facturacionService = facturacionService;
        this.pinStepUpService = pinStepUpService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('caja.facturacion', 'ver')")
    @Operation(summary = "Lista facturas DIAN, con filtros opcionales")
    public FacturasListadoResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                           @RequestParam(required = false) LocalDate fechaFin,
                                           @RequestParam(required = false) String estadoDian,
                                           @RequestParam(required = false) String cliente,
                                           @RequestParam(required = false) String numeroFactura) {
        return FacturasListadoResponse.de(
                facturacionService.listar(fechaInicio, fechaFin, estadoDian, cliente, numeroFactura));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('caja.facturacion', 'ver')")
    @Operation(summary = "Detalle completo de una factura DIAN",
            description = "cufe y qr_code siempre null en esta version — sin transmision real a Factus.")
    public FacturaDetalleResponse obtener(@PathVariable Integer id) {
        return FacturaDetalleResponse.de(facturacionService.detalle(id));
    }

    @PostMapping("/{id}/reenviar-correo")
    @PreAuthorize("hasPermission('caja.facturacion', 'enviar_correo')")
    @Operation(summary = "Reenvia el correo de una factura (stub)",
            description = "Sin proveedor de correo real conectado — solo deja un log INFO del intento.")
    public ReenviarCorreoResponse reenviarCorreo(@PathVariable Integer id) {
        return ReenviarCorreoResponse.de(facturacionService.reenviarCorreo(id));
    }

    @PostMapping("/{id}/reintentar-envio")
    @PreAuthorize("hasPermission('caja.facturacion', 'reintentar_envio')")
    @Operation(summary = "Reintenta la transmision de una factura a la DIAN (stub)",
            description = "Solo aplica si estado_dian es 'pendiente' o 'rechazada'. Sin transmision real a Factus, "
                    + "estado_dian queda igual.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "estado_dian no es 'pendiente' ni 'rechazada'")
    })
    public ReintentarEnvioResponse reintentarEnvio(@PathVariable Integer id) {
        return ReintentarEnvioResponse.de(facturacionService.reintentarEnvio(id));
    }

    @PostMapping("/{id}/anular")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('caja.facturacion', 'generar_nota_credito')")
    @Operation(summary = "Anula una factura DIAN generando una nota credito",
            description = "Requiere PIN de step-up — header X-Pin-Token con el pin_token emitido por "
                    + "POST /auth/pin/verificar para modulo=caja.facturacion, accion=generar_nota_credito, "
                    + "recurso_tipo=factura_dian, recurso_id=el id de esta factura. nota_credito generada SIN "
                    + "devolucion_id (anulacion directa, distinta de una devolucion).")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es valido")
    })
    public AnularFacturaResponse anular(@PathVariable Integer id, @Valid @RequestBody AnularFacturaRequest request,
                                         @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                         @RequestHeader(name = "X-Pin-Token", required = false) String pinToken) {
        pinStepUpService.validar(pinToken, MODULO, ACCION_GENERAR_NOTA_CREDITO, RECURSO_TIPO_FACTURA, id);
        return AnularFacturaResponse.de(facturacionService.anular(id, request.motivo()));
    }
}
