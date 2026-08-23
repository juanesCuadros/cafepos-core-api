package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.MenuPublicoService;
import com.cafepos.core.restaurante.domain.MenuPublicoNoDisponibleException;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint publico (SIN JWT, ver SecurityConfig.apiFilterChain permitAll) —
 * usado por el cliente que escanea el QR. El tenant se resuelve por
 * subdominio/X-Tenant-Slug igual que cualquier otra ruta (TenantFilter
 * corre para TODOS los requests, permitAll solo exime la autenticacion, no
 * la resolucion de tenant).
 */
@RestController
@Tag(name = ApiTags.RESTAURANTE)
public class MenuPublicoController {

    private final MenuPublicoService menuPublicoService;

    public MenuPublicoController(MenuPublicoService menuPublicoService) {
        this.menuPublicoService = menuPublicoService;
    }

    @GetMapping("/menu-publico")
    @Operation(summary = "Menu digital publico del tenant resuelto por subdominio, sin autenticacion")
    public MenuPublicoResponse obtener() {
        return menuPublicoService.obtener()
                .map(MenuPublicoResponse::de)
                .orElseThrow(MenuPublicoNoDisponibleException::new);
    }
}
