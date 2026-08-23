package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.AjusteService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.PinStepUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventario/ajustes")
@Tag(name = ApiTags.INVENTARIO)
public class AjusteController {

    private static final String MODULO = "inventario.existencias";
    private static final String ACCION = "ajustar";
    private static final String RECURSO_TIPO = "insumo";

    private final AjusteService ajusteService;
    private final PinStepUpService pinStepUpService;

    public AjusteController(AjusteService ajusteService, PinStepUpService pinStepUpService) {
        this.ajusteService = ajusteService;
        this.pinStepUpService = pinStepUpService;
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.existencias', 'ajustar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajuste manual de stock desde la vista Existencias",
            description = "Requiere PIN de step-up (ver PinStepUpService) — header X-Pin-Token con el "
                    + "pin_token emitido por POST /auth/pin/verificar para modulo=inventario.existencias, "
                    + "accion=ajustar, recurso_tipo=insumo, recurso_id=el mismo insumo_id de este request.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ajuste aplicado"),
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es "
                    + "valido para este insumo especifico")
    })
    public AjusteResponse ajustar(@Valid @RequestBody AjusteRequest request,
                                   @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                   @RequestHeader(name = "X-Pin-Token", required = false) String pinToken) {
        pinStepUpService.validar(pinToken, MODULO, ACCION, RECURSO_TIPO, request.insumoId());
        return AjusteResponse.de(ajusteService.ajustar(request.insumoId(), request.tipo(), request.cantidad(),
                request.motivo(), request.usuarioAutorizaId()));
    }
}
