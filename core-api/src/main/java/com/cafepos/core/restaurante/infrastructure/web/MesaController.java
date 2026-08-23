package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.ZonaService;
import com.cafepos.core.restaurante.domain.Mesa;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rutas planas a proposito (no anidadas bajo /zonas), asi lo pide el contrato (api_10_restaurante.md 10.2). */
@RestController
@RequestMapping("/mesas")
@Tag(name = ApiTags.RESTAURANTE)
public class MesaController {

    private final ZonaService zonaService;

    public MesaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de una mesa existente")
    public MesaCreadoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody MesaActualizarRequest request) {
        Mesa mesa = zonaService.actualizarMesa(id, request.zonaId(), request.numero(), request.capacidad(),
                request.estado());
        return MesaCreadoResponse.de(mesa);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'eliminar')")
    @Operation(summary = "Elimina una mesa")
    public MesaEliminadaResponse eliminar(@PathVariable Integer id) {
        zonaService.eliminarMesa(id);
        return MesaEliminadaResponse.ELIMINADA;
    }
}
