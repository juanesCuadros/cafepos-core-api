package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.ConteoService;
import com.cafepos.core.inventario.domain.ConteoDetalleInput;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conteos")
@Tag(name = ApiTags.INVENTARIO)
public class ConteoController {

    private final ConteoService conteoService;

    public ConteoController(ConteoService conteoService) {
        this.conteoService = conteoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.conteos', 'ver')")
    @Operation(summary = "Lista los conteos del tenant actual")
    public ConteosResponse listar() {
        return ConteosResponse.de(conteoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.conteos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un conteo completo — calcula diferencias y genera los ajustes automaticamente")
    public ConteoResponse crear(@Valid @RequestBody ConteoCrearRequest request, Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        List<ConteoDetalleInput> detalleInput = request.detalle().stream()
                .map(item -> new ConteoDetalleInput(item.insumoId(), item.stockFisico()))
                .toList();
        return ConteoResponse.de(conteoService.crear(detalleInput, principal.usuarioId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('inventario.conteos', 'ver')")
    @Operation(summary = "Detalle completo de un conteo")
    public ConteoResponse obtener(@PathVariable Integer id) {
        return ConteoResponse.de(conteoService.obtener(id));
    }
}
