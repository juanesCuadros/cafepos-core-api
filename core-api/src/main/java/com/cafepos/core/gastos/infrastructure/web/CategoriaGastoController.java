package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.application.CategoriaGastoService;
import com.cafepos.core.gastos.domain.CategoriaGasto;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Catalogo chico usado desde el modal rapido del formulario de Gastos — solo GET/POST, sin PATCH/DELETE. */
@RestController
@RequestMapping("/categorias-gasto")
@Tag(name = ApiTags.GASTOS)
public class CategoriaGastoController {

    private final CategoriaGastoService categoriaGastoService;

    public CategoriaGastoController(CategoriaGastoService categoriaGastoService) {
        this.categoriaGastoService = categoriaGastoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('gastos.registrar_gasto', 'ver')")
    @Operation(summary = "Lista las categorias de gasto del tenant actual")
    public CategoriasGastoResponse listar() {
        return CategoriasGastoResponse.de(categoriaGastoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('gastos.registrar_gasto', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una categoria de gasto")
    public CategoriaGastoCreadaResponse crear(@Valid @RequestBody CategoriaGastoCrearRequest request) {
        CategoriaGasto categoriaGasto = categoriaGastoService.crear(request.nombre());
        return CategoriaGastoCreadaResponse.de(categoriaGasto);
    }
}
