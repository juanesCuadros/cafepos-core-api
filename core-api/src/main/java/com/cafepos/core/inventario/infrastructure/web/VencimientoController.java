package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.VencimientoService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Solo lectura. lote_insumo hoy esta vacia en la practica (se llena desde
 * Compras, que no existe todavia) — este endpoint funciona igual y
 * devuelve una lista vacia hasta que ese modulo exista, no es un bug.
 */
@RestController
@RequestMapping("/lotes-insumo/vencimientos")
@Tag(name = ApiTags.INVENTARIO)
public class VencimientoController {

    private final VencimientoService vencimientoService;

    public VencimientoController(VencimientoService vencimientoService) {
        this.vencimientoService = vencimientoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.vencimientos', 'ver')")
    @Operation(summary = "Lotes proximos a vencer o vencidos — uno por insumo, el mas urgente")
    public VencimientosResponse listar(@RequestParam(required = false) String estado,
                                        @RequestParam(name = "categoria_insumo_id", required = false) Integer categoriaInsumoId) {
        return VencimientosResponse.de(vencimientoService.listar(estado, categoriaInsumoId));
    }
}
