package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.DevolucionService;
import com.cafepos.core.caja.application.ItemDevolucionInput;
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

/**
 * Devoluciones (api_03_caja.md 3.7). El catalogo marca requiere_pin=true
 * tanto en 'solicitar' como en 'autorizar' para caja.devoluciones, pero
 * rol_permiso solo le da 'autorizar' a Admin/Jefe (Cajero solo tiene
 * 'solicitar') — el PIN de este endpoint valida contra 'autorizar', que es
 * la que efectivamente representa la autorizacion de un superior en este
 * flujo de un solo paso (ver DevolucionService).
 */
@RestController
@RequestMapping("/devoluciones")
@Tag(name = ApiTags.CAJA)
public class DevolucionController {

    private static final String MODULO = "caja.devoluciones";
    private static final String ACCION_AUTORIZAR = "autorizar";
    private static final String RECURSO_TIPO_VENTA = "venta";

    private final DevolucionService devolucionService;
    private final PinStepUpService pinStepUpService;

    public DevolucionController(DevolucionService devolucionService, PinStepUpService pinStepUpService) {
        this.devolucionService = devolucionService;
        this.pinStepUpService = pinStepUpService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('caja.devoluciones', 'ver')")
    @Operation(summary = "Lista devoluciones, con filtros opcionales")
    public DevolucionesListadoResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                               @RequestParam(required = false) LocalDate fechaFin,
                                               @RequestParam(required = false) String estado) {
        return DevolucionesListadoResponse.de(devolucionService.listar(fechaInicio, fechaFin, estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('caja.devoluciones', 'ver')")
    @Operation(summary = "Detalle completo de una devolucion, con sus items")
    public DevolucionDetalleResponse obtener(@PathVariable Integer id) {
        return DevolucionDetalleResponse.de(devolucionService.detalle(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('caja.devoluciones', 'solicitar')")
    @Operation(summary = "Solicita una devolucion sobre una venta",
            description = "Requiere PIN de step-up — header X-Pin-Token con el pin_token emitido por "
                    + "POST /auth/pin/verificar para modulo=caja.devoluciones, accion=autorizar, "
                    + "recurso_tipo=venta, recurso_id=venta_id del body. metodo_reembolso se determina "
                    + "automaticamente segun el estado_preparacion de los items devueltos. "
                    + "usuario_autoriza_id es opcional: si no viene, se toma el usuario autenticado "
                    + "del token (caso Jefe autoconfirmando sin PIN de un tercero).")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es valido"),
            @ApiResponse(responseCode = "404", description = "Venta o pedido_item_id no encontrado")
    })
    public SolicitarDevolucionResponse solicitar(@Valid @RequestBody SolicitarDevolucionRequest request,
                                                  @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                                  @RequestHeader(name = "X-Pin-Token", required = false) String pinToken,
                                                  Authentication authentication) {
        pinStepUpService.validar(pinToken, MODULO, ACCION_AUTORIZAR, RECURSO_TIPO_VENTA, request.ventaId());
        var items = request.items().stream()
                .map(i -> new ItemDevolucionInput(i.pedidoItemId(), i.cantidad()))
                .toList();
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        Integer usuarioAutorizaId = request.usuarioAutorizaId() != null ? request.usuarioAutorizaId() : principal.usuarioId();
        return SolicitarDevolucionResponse.de(devolucionService.solicitar(request.ventaId(), items, request.motivo(),
                usuarioAutorizaId));
    }
}
