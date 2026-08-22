package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.application.CategoriaService;
import com.cafepos.core.productosmenu.domain.Categoria;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@Tag(name = ApiTags.PRODUCTOS_MENU)
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('productos_menu.categorias', 'ver')")
    @Operation(summary = "Lista las categorias del tenant actual, con el numero de productos de cada una")
    public CategoriasResponse listar() {
        return CategoriasResponse.de(categoriaService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('productos_menu.categorias', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una categoria")
    public CategoriaResponse crear(@Valid @RequestBody CategoriaCrearRequest request) {
        Categoria categoria = categoriaService.crear(request.icono(), request.nombre(),
                request.descripcion(), request.orden(), request.estado());
        return CategoriaResponse.de(categoria);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.categorias', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de una categoria existente")
    public CategoriaResponse actualizar(@PathVariable Integer id, @RequestBody CategoriaActualizarRequest request) {
        Categoria categoria = categoriaService.actualizar(id, request.icono(), request.nombre(),
                request.descripcion(), request.orden(), request.estado());
        return CategoriaResponse.de(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.categorias', 'eliminar')")
    @Operation(summary = "Elimina una categoria sin productos asociados")
    public CategoriaEliminadaResponse eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return CategoriaEliminadaResponse.ELIMINADA;
    }
}
