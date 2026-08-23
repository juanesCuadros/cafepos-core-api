package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.PerdidaService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/perdidas")
@Tag(name = ApiTags.INVENTARIO)
public class PerdidaController {

    private final PerdidaService perdidaService;

    public PerdidaController(PerdidaService perdidaService) {
        this.perdidaService = perdidaService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.perdidas', 'ver')")
    @Operation(summary = "Lista las perdidas del tenant actual, con filtros opcionales")
    public PerdidasResponse listar(@RequestParam(name = "fecha_inicio", required = false) LocalDate fechaInicio,
                                    @RequestParam(name = "fecha_fin", required = false) LocalDate fechaFin,
                                    @RequestParam(name = "categoria_insumo_id", required = false) Integer categoriaInsumoId,
                                    @RequestParam(required = false) String motivo) {
        return PerdidasResponse.de(perdidaService.listar(fechaInicio, fechaFin, categoriaInsumoId, motivo));
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.perdidas', 'registrar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una perdida — descuenta stock y calcula el costo automaticamente")
    public PerdidaCreadoResponse registrar(@Valid @RequestBody PerdidaCrearRequest request,
                                            Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return PerdidaCreadoResponse.de(perdidaService.registrar(request.insumoId(), request.cantidad(),
                request.motivo(), request.fecha(), request.observaciones(), principal.usuarioId()));
    }
}
