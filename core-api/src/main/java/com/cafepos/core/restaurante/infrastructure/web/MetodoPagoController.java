package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.MetodoPagoService;
import com.cafepos.core.restaurante.domain.MetodoPago;
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
@RequestMapping("/metodos-pago")
@Tag(name = ApiTags.RESTAURANTE)
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('restaurante.metodos_pago', 'ver')")
    @Operation(summary = "Lista los metodos de pago del tenant actual")
    public MetodosPagoResponse listar() {
        return MetodosPagoResponse.de(metodoPagoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('restaurante.metodos_pago', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un metodo de pago")
    public MetodoPagoCreadoResponse crear(@Valid @RequestBody MetodoPagoCrearRequest request) {
        MetodoPago metodoPago = metodoPagoService.crear(request.nombre(), request.icono(), request.estado());
        return MetodoPagoCreadoResponse.de(metodoPago);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('restaurante.metodos_pago', 'editar')")
    @Operation(summary = "Actualiza un metodo de pago — Efectivo no se puede pasar a estado inactivo")
    public MetodoPagoCreadoResponse actualizar(@PathVariable Integer id,
                                                @Valid @RequestBody MetodoPagoActualizarRequest request) {
        MetodoPago metodoPago = metodoPagoService.actualizar(id, request.icono(), request.nombre(),
                request.estado());
        return MetodoPagoCreadoResponse.de(metodoPago);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('restaurante.metodos_pago', 'eliminar')")
    @Operation(summary = "Elimina un metodo de pago — Efectivo no se puede eliminar")
    public MetodoPagoEliminadoResponse eliminar(@PathVariable Integer id) {
        metodoPagoService.eliminar(id);
        return MetodoPagoEliminadoResponse.ELIMINADO;
    }
}
