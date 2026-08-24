package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.CajaHistorialService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/caja/jornadas")
@Tag(name = ApiTags.CAJA)
public class CajaHistorialController {

    private final CajaHistorialService cajaHistorialService;

    public CajaHistorialController(CajaHistorialService cajaHistorialService) {
        this.cajaHistorialService = cajaHistorialService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('caja.historial_caja', 'ver')")
    @Operation(summary = "Historial de jornadas de caja (solo dentro del rango de fechas dado)")
    public JornadasHistorialResponse listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return JornadasHistorialResponse.de(cajaHistorialService.listarJornadas(fechaInicio, fechaFin));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('caja.historial_caja', 'ver')")
    @Operation(summary = "Detalle completo de una jornada, con todos los movimientos")
    public JornadaDetalleResponse detalle(@PathVariable Integer id) {
        return JornadaDetalleResponse.de(cajaHistorialService.detalle(id));
    }
}
