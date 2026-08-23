package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.CategoriaInsumoService;
import com.cafepos.core.inventario.domain.CategoriaInsumo;
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

/** Catalogo chico usado desde el modal rapido del formulario de Insumos — solo GET/POST, sin PATCH/DELETE. */
@RestController
@RequestMapping("/categorias-insumo")
@Tag(name = ApiTags.INVENTARIO)
public class CategoriaInsumoController {

    private final CategoriaInsumoService categoriaInsumoService;

    public CategoriaInsumoController(CategoriaInsumoService categoriaInsumoService) {
        this.categoriaInsumoService = categoriaInsumoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.insumos', 'ver')")
    @Operation(summary = "Lista las categorias de insumo del tenant actual")
    public CategoriasInsumoResponse listar() {
        return CategoriasInsumoResponse.de(categoriaInsumoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.insumos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una categoria de insumo")
    public CategoriaInsumoResponse crear(@Valid @RequestBody CategoriaInsumoCrearRequest request) {
        CategoriaInsumo categoriaInsumo = categoriaInsumoService.crear(request.nombre());
        return CategoriaInsumoResponse.de(categoriaInsumo);
    }
}
