package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.AjusteService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * PENDIENTE (a proposito): el contrato pide PIN de step-up aca (accion
 * "ajuste_inventario"). Esa infraestructura no existe todavia en codigo -
 * ver Javadoc de AjusteService para el detalle completo de este diferido.
 */
@RestController
@RequestMapping("/inventario/ajustes")
@Tag(name = ApiTags.INVENTARIO)
public class AjusteController {

    private final AjusteService ajusteService;

    public AjusteController(AjusteService ajusteService) {
        this.ajusteService = ajusteService;
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.existencias', 'ajustar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajuste manual de stock desde la vista Existencias")
    public AjusteResponse ajustar(@Valid @RequestBody AjusteRequest request) {
        return AjusteResponse.de(ajusteService.ajustar(request.insumoId(), request.tipo(), request.cantidad(),
                request.motivo(), request.usuarioAutorizaId()));
    }
}
