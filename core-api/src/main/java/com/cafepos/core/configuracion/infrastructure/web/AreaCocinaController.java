package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.application.AreaCocinaService;
import com.cafepos.core.configuracion.domain.AreaCocina;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CRUD simple, sin reglas de asociacion (a diferencia de zona/producto) — no pedido por el contrato. */
@RestController
@RequestMapping("/areas-cocina")
@Tag(name = ApiTags.CONFIGURACION)
public class AreaCocinaController {

    private final AreaCocinaService areaCocinaService;

    public AreaCocinaController(AreaCocinaService areaCocinaService) {
        this.areaCocinaService = areaCocinaService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'ver')")
    @Operation(summary = "Lista las areas de cocina del tenant actual")
    public AreasCocinaResponse listar() {
        return AreasCocinaResponse.de(areaCocinaService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un area de cocina")
    public AreaCocinaResponse crear(@Valid @RequestBody AreaCocinaCrearRequest request) {
        AreaCocina areaCocina = areaCocinaService.crear(request.nombre(), request.estado());
        return AreaCocinaResponse.de(areaCocina);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Actualiza un area de cocina")
    public AreaCocinaResponse actualizar(@PathVariable Integer id, @RequestBody AreaCocinaActualizarRequest request) {
        AreaCocina areaCocina = areaCocinaService.actualizar(id, request.nombre(), request.estado());
        return AreaCocinaResponse.de(areaCocina);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Elimina un area de cocina")
    public AreaCocinaEliminadaResponse eliminar(@PathVariable Integer id) {
        areaCocinaService.eliminar(id);
        return AreaCocinaEliminadaResponse.ELIMINADA;
    }
}
