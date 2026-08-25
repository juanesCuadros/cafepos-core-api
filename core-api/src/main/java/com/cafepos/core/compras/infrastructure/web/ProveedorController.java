package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.application.ProveedorService;
import com.cafepos.core.compras.domain.Proveedor;
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

@RestController
@RequestMapping("/proveedores")
@Tag(name = ApiTags.COMPRAS)
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('compras.proveedores', 'ver')")
    @Operation(summary = "Lista proveedores, con filtros opcionales")
    public ProveedoresResponse listar(@RequestParam(required = false) String estado,
                                       @RequestParam(required = false) String q) {
        return ProveedoresResponse.de(proveedorService.listar(estado, q));
    }

    @PostMapping
    @PreAuthorize("hasPermission('compras.proveedores', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un proveedor")
    public ProveedorCreadoResponse crear(@Valid @RequestBody ProveedorCrearRequest request) {
        Proveedor proveedor = proveedorService.crear(request.nombre(), request.nit(), request.contacto(),
                request.telefono(), request.correo(), request.direccion(), request.estado());
        return ProveedorCreadoResponse.de(proveedor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('compras.proveedores', 'ver')")
    @Operation(summary = "Detalle completo de un proveedor")
    public ProveedorResponse obtener(@PathVariable Integer id) {
        return ProveedorResponse.de(proveedorService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('compras.proveedores', 'editar')")
    @Operation(summary = "Actualiza un proveedor")
    public ProveedorResponse actualizar(@PathVariable Integer id,
                                         @RequestBody ProveedorActualizarRequest request) {
        Proveedor proveedor = proveedorService.actualizar(id, request.nombre(), request.nit(), request.contacto(),
                request.telefono(), request.correo(), request.direccion(), request.estado());
        return ProveedorResponse.de(proveedor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('compras.proveedores', 'eliminar')")
    @Operation(summary = "Elimina un proveedor",
            description = "409 si el proveedor tiene compras asociadas.")
    public ProveedorEliminadoResponse eliminar(@PathVariable Integer id) {
        proveedorService.eliminar(id);
        return ProveedorEliminadoResponse.ELIMINADO;
    }
}
