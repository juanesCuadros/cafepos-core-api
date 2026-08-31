package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.application.ProductoService;
import com.cafepos.core.productosmenu.domain.Producto;
import com.cafepos.core.productosmenu.domain.ResultadoEliminacionProducto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/productos")
@Tag(name = ApiTags.PRODUCTOS_MENU)
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('productos_menu.productos', 'ver')")
    @Operation(summary = "Lista los productos del tenant actual, con filtros opcionales")
    public ProductosResponse listar(@RequestParam(name = "categoria_id", required = false) Integer categoriaId,
                                     @RequestParam(required = false) String estado,
                                     @RequestParam(required = false) String q) {
        return ProductosResponse.de(productoService.listar(categoriaId, estado, q));
    }

    @PostMapping
    @PreAuthorize("hasPermission('productos_menu.productos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un producto")
    public ProductoCreadoResponse crear(@Valid @RequestBody ProductoCrearRequest request) {
        Producto producto = productoService.crear(request.categoriaId(), request.areaCocinaId(), request.nombre(),
                request.descripcion(), request.imagen(), request.precioVenta(), request.costoEstimado(),
                request.tasaImpuesto(), request.manejaReceta(), request.manejaInventario(), request.unidadMedida(),
                request.stockMinimo(), request.estado(), request.visibilidad());
        return ProductoCreadoResponse.de(producto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.productos', 'ver')")
    @Operation(summary = "Detalle completo de un producto")
    public ProductoDetalleResponse obtener(@PathVariable Integer id) {
        return ProductoDetalleResponse.de(productoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.productos', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de un producto existente")
    public ProductoCreadoResponse actualizar(@PathVariable Integer id,
                                              @Valid @RequestBody ProductoActualizarRequest request) {
        Producto producto = productoService.actualizar(id, request.categoriaId(), request.areaCocinaId(),
                request.nombre(), request.descripcion(), request.imagen(), request.precioVenta(),
                request.costoEstimado(), request.tasaImpuesto(), request.manejaReceta(), request.manejaInventario(),
                request.unidadMedida(), request.stockMinimo(), request.estado(), request.visibilidad());
        return ProductoCreadoResponse.de(producto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.productos', 'eliminar')")
    @Operation(summary = "Elimina un producto sin ventas asociadas, o lo marca inactivo si ya tiene ventas")
    public ProductoEliminadoResponse eliminar(@PathVariable Integer id) {
        ResultadoEliminacionProducto resultado = productoService.eliminar(id);
        return resultado == ResultadoEliminacionProducto.MARCADO_INACTIVO
                ? ProductoEliminadoResponse.marcadoInactivo()
                : ProductoEliminadoResponse.ELIMINADO;
    }

    @PostMapping(value = "/{id}/imagen", consumes = "multipart/form-data")
    @PreAuthorize("hasPermission('productos_menu.productos', 'editar')")
    @Operation(summary = "Sube la imagen de un producto")
    public ProductoImagenResponse subirImagen(@PathVariable Integer id, @RequestParam("archivo") MultipartFile archivo, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build().toUriString();
        String urlCompleta = productoService.subirImagen(id, archivo, baseUrl);
        return new ProductoImagenResponse(urlCompleta);
    }
}
