package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.application.PromocionService;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.Promocion;
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

import java.util.List;

@RestController
@RequestMapping("/promociones")
@Tag(name = ApiTags.PRODUCTOS_MENU)
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('productos_menu.promociones', 'ver')")
    @Operation(summary = "Lista las promociones del tenant actual")
    public PromocionesResponse listar() {
        return PromocionesResponse.de(promocionService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('productos_menu.promociones', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una promocion")
    public PromocionCreadoResponse crear(@Valid @RequestBody PromocionCrearRequest request) {
        Promocion promocion = promocionService.crear(request.nombre(), request.tipoDescuento(),
                request.valorDescuento(), request.aplicaA(), request.productosIds(), request.vigenciaInicio(),
                request.vigenciaFin(), request.diasSemana(), request.horaInicio(), request.horaFin(),
                request.cantidadMinima(), request.montoMinimo(), request.estado());
        List<ProductoRef> productos = promocionService.productosDe(promocion.getId());
        return PromocionCreadoResponse.de(promocion, productos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.promociones', 'ver')")
    @Operation(summary = "Detalle completo de una promocion")
    public PromocionDetalleResponse obtener(@PathVariable Integer id) {
        Promocion promocion = promocionService.buscarPorId(id);
        List<ProductoRef> productos = promocionService.productosDe(id);
        return PromocionDetalleResponse.de(promocion, productos);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.promociones', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de una promocion existente")
    public PromocionCreadoResponse actualizar(@PathVariable Integer id,
                                               @Valid @RequestBody PromocionActualizarRequest request) {
        Promocion promocion = promocionService.actualizar(id, request.nombre(), request.tipoDescuento(),
                request.valorDescuento(), request.aplicaA(), request.productosIds(), request.vigenciaInicio(),
                request.vigenciaFin(), request.diasSemana(), request.horaInicio(), request.horaFin(),
                request.cantidadMinima(), request.montoMinimo(), request.estado());
        List<ProductoRef> productos = promocionService.productosDe(id);
        return PromocionCreadoResponse.de(promocion, productos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.promociones', 'eliminar')")
    @Operation(summary = "Elimina una promocion")
    public PromocionEliminadaResponse eliminar(@PathVariable Integer id) {
        promocionService.eliminar(id);
        return PromocionEliminadaResponse.ELIMINADA;
    }
}
