package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsService;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operacion/kds")
@Tag(name = ApiTags.OPERACION)
public class KdsController {

    private final KdsService kdsService;

    public KdsController(KdsService kdsService) {
        this.kdsService = kdsService;
    }

    @GetMapping("/pedidos")
    @PreAuthorize("hasPermission('operacion.kds', 'ver')")
    @Operation(summary = "Pedidos con comanda enviada, para la pantalla de cocina")
    public KdsPedidosResponse listar(@RequestParam(required = false) String estado) {
        return KdsPedidosResponse.de(kdsService.listar(estado));
    }

    @PatchMapping("/items/{itemId}/estado")
    @PreAuthorize("hasPermission('operacion.kds', 'cambiar_estado_item')")
    @Operation(summary = "Cambia el estado de preparacion de un item",
            description = "Solo hacia adelante: pendiente -> en_preparacion -> listo.")
    @ApiResponses({@ApiResponse(responseCode = "400", description = "Transicion invalida (retrocede o repite estado)")})
    public KdsItemEstadoResponse cambiarEstado(@PathVariable Integer itemId,
                                                @Valid @RequestBody KdsItemEstadoRequest request) {
        return KdsItemEstadoResponse.de(kdsService.cambiarEstadoItem(itemId, request.estadoPreparacion()));
    }
}
