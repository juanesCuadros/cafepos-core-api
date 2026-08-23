package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.MenuDigitalService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurante/menu-digital")
@Tag(name = ApiTags.RESTAURANTE)
public class MenuDigitalController {

    private final MenuDigitalService menuDigitalService;

    public MenuDigitalController(MenuDigitalService menuDigitalService) {
        this.menuDigitalService = menuDigitalService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('restaurante.menu_digital', 'ver')")
    @Operation(summary = "Estado del menu digital y su QR (generado al vuelo)")
    public MenuDigitalResponse obtener() {
        return MenuDigitalResponse.de(menuDigitalService.obtener());
    }

    @PatchMapping
    @PreAuthorize("hasPermission('restaurante.menu_digital', 'activar_desactivar')")
    @Operation(summary = "Activa o desactiva el menu digital publico")
    public MenuDigitalResponse actualizar(@Valid @RequestBody MenuDigitalActualizarRequest request) {
        return MenuDigitalResponse.de(menuDigitalService.actualizar(request.activo()));
    }
}
