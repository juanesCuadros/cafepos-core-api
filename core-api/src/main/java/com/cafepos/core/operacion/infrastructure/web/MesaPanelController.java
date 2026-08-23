package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.MesasPanelService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operacion/mesas")
@Tag(name = ApiTags.OPERACION)
public class MesaPanelController {

    private final MesasPanelService mesasPanelService;

    public MesaPanelController(MesasPanelService mesasPanelService) {
        this.mesasPanelService = mesasPanelService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('operacion.pedidos', 'ver')")
    @Operation(summary = "Panel de mesas: todas las zonas con sus mesas y estado actual")
    public MesasPanelResponse listar() {
        return MesasPanelResponse.de(mesasPanelService.listar());
    }
}
