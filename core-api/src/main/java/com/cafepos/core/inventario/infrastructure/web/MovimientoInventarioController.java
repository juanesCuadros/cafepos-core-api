package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.MovimientoInventarioService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/** Solo lectura — sin POST/PATCH/DELETE, los movimientos los genera el sistema (ajustes, perdidas, conteos). */
@RestController
@RequestMapping("/movimientos-inventario")
@Tag(name = ApiTags.INVENTARIO)
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.historial_movimientos', 'ver')")
    @Operation(summary = "Historial de movimientos de inventario, con filtros opcionales")
    public MovimientosResponse listar(@RequestParam(name = "fecha_inicio", required = false) LocalDate fechaInicio,
                                       @RequestParam(name = "fecha_fin", required = false) LocalDate fechaFin,
                                       @RequestParam(required = false) String tipo,
                                       @RequestParam(name = "insumo_id", required = false) Integer insumoId,
                                       @RequestParam(name = "usuario_id", required = false) Integer usuarioId) {
        return MovimientosResponse.de(
                movimientoInventarioService.listar(fechaInicio, fechaFin, tipo, insumoId, usuarioId));
    }
}
