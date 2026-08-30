package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.application.GastoService;
import com.cafepos.core.gastos.domain.Gasto;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/gastos")
@Tag(name = ApiTags.GASTOS)
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('gastos.historial_gastos', 'ver')")
    @Operation(summary = "Lista gastos, con filtros opcionales")
    public GastosResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                  @RequestParam(required = false) LocalDate fechaFin,
                                  @RequestParam(required = false) Integer categoriaGastoId,
                                  @RequestParam(required = false) String metodoPago) {
        return GastosResponse.de(gastoService.listar(fechaInicio, fechaFin, categoriaGastoId, metodoPago));
    }

    @PostMapping
    @PreAuthorize("hasPermission('gastos.registrar_gasto', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un gasto",
            description = "404 si categoria_gasto_id no existe, 400 si existe pero esta inactiva.")
    public GastoCreadoResponse crear(@Valid @RequestBody GastoCrearRequest request, Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        Gasto gasto = gastoService.crear(request.categoriaGastoId(), request.descripcion(), request.monto(),
                request.metodoPago(), request.fecha(), request.comprobanteImagen(), request.observaciones(),
                principal.usuarioId());
        return GastoCreadoResponse.de(gasto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('gastos.historial_gastos', 'ver')")
    @Operation(summary = "Detalle completo de un gasto, incluido el comprobante")
    public GastoDetalleResponse obtener(@PathVariable Integer id) {
        return GastoDetalleResponse.de(gastoService.detalle(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('gastos.historial_gastos', 'editar')")
    @Operation(summary = "Actualiza un gasto", description = "Mismos campos del POST, opcionales.")
    public GastoCreadoResponse actualizar(@PathVariable Integer id, @RequestBody GastoActualizarRequest request) {
        Gasto gasto = gastoService.actualizar(id, request.categoriaGastoId(), request.descripcion(),
                request.monto(), request.metodoPago(), request.fecha(), request.comprobanteImagen(),
                request.observaciones());
        return GastoCreadoResponse.de(gasto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('gastos.historial_gastos', 'eliminar')")
    @Operation(summary = "Elimina un gasto (borrado fisico)", description = "Solo Jefe segun el catalogo de permisos.")
    public GastoEliminadoResponse eliminar(@PathVariable Integer id) {
        gastoService.eliminar(id);
        return GastoEliminadoResponse.INSTANCIA;
    }
}
