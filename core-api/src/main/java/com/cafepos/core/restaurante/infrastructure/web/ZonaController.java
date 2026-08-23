package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.ZonaService;
import com.cafepos.core.restaurante.domain.Mesa;
import com.cafepos.core.restaurante.domain.Zona;
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

/**
 * Mesa no tiene entrada propia en el catalogo de permisos — comparte
 * restaurante.zonas_mesas con Zona (ver ZonaService). Por eso GET/POST de
 * mesas viven aca, anidados bajo /zonas/{id}, aunque PATCH/DELETE de mesa
 * sean rutas planas en MesaController (asi lo pide el contrato).
 */
@RestController
@Tag(name = ApiTags.RESTAURANTE)
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping("/zonas")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'ver')")
    @Operation(summary = "Lista las zonas del tenant actual, con el numero de mesas de cada una")
    public ZonasResponse listar() {
        return ZonasResponse.de(zonaService.listar());
    }

    @PostMapping("/zonas")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una zona")
    public ZonaCreadoResponse crear(@Valid @RequestBody ZonaCrearRequest request) {
        Zona zona = zonaService.crear(request.icono(), request.nombre(), request.estado());
        return ZonaCreadoResponse.de(zona);
    }

    @PatchMapping("/zonas/{id}")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de una zona existente")
    public ZonaCreadoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ZonaActualizarRequest request) {
        Zona zona = zonaService.actualizar(id, request.icono(), request.nombre(), request.estado());
        return ZonaCreadoResponse.de(zona);
    }

    @DeleteMapping("/zonas/{id}")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'eliminar')")
    @Operation(summary = "Elimina una zona sin mesas asociadas")
    public ZonaEliminadaResponse eliminar(@PathVariable Integer id) {
        zonaService.eliminar(id);
        return ZonaEliminadaResponse.ELIMINADA;
    }

    @GetMapping("/zonas/{id}/mesas")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'ver')")
    @Operation(summary = "Lista las mesas de una zona")
    public MesasResponse listarMesas(@PathVariable Integer id) {
        return MesasResponse.de(zonaService.listarMesas(id));
    }

    @PostMapping("/zonas/{id}/mesas")
    @PreAuthorize("hasPermission('restaurante.zonas_mesas', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una mesa dentro de una zona")
    public MesaCreadoResponse crearMesa(@PathVariable Integer id, @Valid @RequestBody MesaCrearRequest request) {
        Mesa mesa = zonaService.crearMesa(id, request.numero(), request.capacidad(), request.estado());
        return MesaCreadoResponse.de(mesa);
    }
}
