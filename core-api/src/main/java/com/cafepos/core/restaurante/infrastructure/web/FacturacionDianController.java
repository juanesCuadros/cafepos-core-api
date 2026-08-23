package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.FacturacionDianService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Solo lectura a proposito — no existe POST/PATCH para este recurso, ni para Jefe ni para nadie (contrato 10.4). */
@RestController
@RequestMapping("/restaurante/facturacion-dian")
@Tag(name = ApiTags.RESTAURANTE)
public class FacturacionDianController {

    private final FacturacionDianService facturacionDianService;

    public FacturacionDianController(FacturacionDianService facturacionDianService) {
        this.facturacionDianService = facturacionDianService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('restaurante.facturacion_dian', 'ver')")
    @Operation(summary = "Estado de la configuracion de facturacion electronica DIAN (solo lectura)")
    public FacturacionDianResponse obtener() {
        return FacturacionDianResponse.de(facturacionDianService.obtener());
    }
}
