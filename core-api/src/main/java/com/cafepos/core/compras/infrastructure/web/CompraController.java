package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.application.CompraService;
import com.cafepos.core.compras.application.CompraVista;
import com.cafepos.core.compras.application.DetalleCompraInput;
import com.cafepos.core.compras.domain.Compra;
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
 * POST /compras/{id}/anular usa hasPermission('compras.historial_compras',
 * 'eliminar') — el catalogo real no tiene una accion 'anular' para este
 * modulo (verificado en tabla permiso antes de escribir esto); 'eliminar'
 * es la accion mas cercana semanticamente (anular es la unica forma de
 * "remover" una compra, nunca hay DELETE fisico) y ademas es la unica con
 * requiere_pin=true de las cuatro de compras.historial_compras (confirmado
 * en tenant_permiso_config Y en la lista fija de admin-api.CrearNegocioService).
 */
@RestController
@RequestMapping("/compras")
@Tag(name = ApiTags.COMPRAS)
public class CompraController {

    private static final String MODULO_HISTORIAL = "compras.historial_compras";
    private static final String ACCION_ANULAR = "eliminar";
    private static final String RECURSO_TIPO_COMPRA = "compra";

    private final CompraService compraService;
    private final PinStepUpService pinStepUpService;

    public CompraController(CompraService compraService, PinStepUpService pinStepUpService) {
        this.compraService = compraService;
        this.pinStepUpService = pinStepUpService;
    }

    @PostMapping
    @PreAuthorize("hasPermission('compras.registrar_compra', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una compra — suma stock, sobreescribe costo_actual y genera lotes por cada linea")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Algun insumo_id del detalle no existe"),
            @ApiResponse(responseCode = "404", description = "proveedor_id no existe")
    })
    public CompraCreadoResponse registrar(@Valid @RequestBody CompraCrearRequest request,
                                           Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        var detalles = request.detalle().stream()
                .map(d -> new DetalleCompraInput(d.insumoId(), d.cantidad(), d.costoUnitario(), d.numeroLote(),
                        d.fechaVencimiento()))
                .toList();
        Compra compra = compraService.registrar(request.proveedorId(), request.numeroFacturaProv(), request.fecha(),
                request.formaPago(), request.observaciones(), detalles, principal.usuarioId());
        return CompraCreadoResponse.de(compra);
    }

    @GetMapping
    @PreAuthorize("hasPermission('compras.historial_compras', 'ver')")
    @Operation(summary = "Historial de compras, con filtros opcionales")
    public ComprasResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                   @RequestParam(required = false) LocalDate fechaFin,
                                   @RequestParam(required = false) Integer proveedorId,
                                   @RequestParam(required = false) String formaPago,
                                   @RequestParam(required = false) String estado) {
        return ComprasResponse.de(compraService.listar(fechaInicio, fechaFin, proveedorId, formaPago, estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('compras.historial_compras', 'ver')")
    @Operation(summary = "Detalle completo de una compra")
    public CompraDetalleResponse obtener(@PathVariable Integer id) {
        CompraVista vista = compraService.detalle(id);
        return CompraDetalleResponse.de(vista);
    }

    @PostMapping("/{id}/anular")
    @PreAuthorize("hasPermission('compras.historial_compras', 'eliminar')")
    @Operation(summary = "Anula una compra — revierte stock, costo_actual y agota los lotes que genero",
            description = "Requiere PIN de step-up — header X-Pin-Token con el pin_token emitido por "
                    + "POST /auth/pin/verificar para modulo=compras.historial_compras, accion=eliminar, "
                    + "recurso_tipo=compra, recurso_id=el id de esta compra. Bloqueada (403 de negocio) solo si "
                    + "la compra es de credito y ya esta pagada — una de contado siempre se puede anular.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Revertir el stock de alguna linea lo dejaria negativo"),
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token / pin_token invalido, o "
                    + "la compra es de credito y ya esta pagada")
    })
    public AnularCompraResponse anular(@PathVariable Integer id,
                                        @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                        @RequestHeader(name = "X-Pin-Token", required = false) String pinToken,
                                        Authentication authentication) {
        pinStepUpService.validar(pinToken, MODULO_HISTORIAL, ACCION_ANULAR, RECURSO_TIPO_COMPRA, id);
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return AnularCompraResponse.de(compraService.anular(id, principal.usuarioId()));
    }

    @PostMapping("/{id}/marcar-pagada")
    @PreAuthorize("hasPermission('compras.historial_compras', 'marcar_pagada')")
    @Operation(summary = "Marca una compra de credito como pagada",
            description = "Sin PIN — no esta en la lista de acciones que lo exigen. 400 si la compra no es de "
                    + "credito, o si ya no esta pendiente.")
    public MarcarPagadaResponse marcarPagada(@PathVariable Integer id) {
        return MarcarPagadaResponse.de(compraService.marcarPagada(id));
    }
}
