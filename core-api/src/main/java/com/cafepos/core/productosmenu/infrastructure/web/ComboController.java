package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.application.ComboService;
import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.productosmenu.domain.ComboGrupoCrear;
import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;
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
@RequestMapping("/combos")
@Tag(name = ApiTags.PRODUCTOS_MENU)
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('productos_menu.combos', 'ver')")
    @Operation(summary = "Lista los combos del tenant actual")
    public CombosResponse listar() {
        return CombosResponse.de(comboService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('productos_menu.combos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un combo con sus grupos y productos en una sola llamada, transaccional")
    public ComboDetalleResponse crear(@Valid @RequestBody ComboCrearRequest request) {
        Combo combo = comboService.crear(request.nombre(), request.descripcion(), request.imagen(),
                request.precio(), request.estado(), aGruposCrear(request.grupos()));
        return respuestaDetalle(combo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'ver')")
    @Operation(summary = "Detalle completo de un combo, con grupos y productos anidados")
    public ComboDetalleResponse obtener(@PathVariable Integer id) {
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @Operation(summary = "Actualiza solo los datos generales del combo — ignora \"grupos\" si viene en el body")
    public ComboDetalleResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ComboActualizarRequest request) {
        Combo combo = comboService.actualizar(id, request.nombre(), request.descripcion(), request.imagen(),
                request.precio(), request.estado());
        return respuestaDetalle(combo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'eliminar')")
    @Operation(summary = "Elimina un combo (fisico) — grupos y productos asociados se borran en cascada")
    public ComboEliminadoResponse eliminar(@PathVariable Integer id) {
        comboService.eliminar(id);
        return ComboEliminadoResponse.ELIMINADO;
    }

    @PostMapping("/{id}/grupos")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un grupo nuevo (sin productos) dentro de un combo existente")
    public ComboDetalleResponse crearGrupo(@PathVariable Integer id, @Valid @RequestBody ComboGrupoNombreRequest request) {
        comboService.crearGrupo(id, request.nombre());
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    @PatchMapping("/{id}/grupos/{grupoId}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @Operation(summary = "Renombra un grupo de un combo existente")
    public ComboDetalleResponse renombrarGrupo(@PathVariable Integer id, @PathVariable Integer grupoId,
                                                @Valid @RequestBody ComboGrupoNombreRequest request) {
        comboService.renombrarGrupo(id, grupoId, request.nombre());
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    @DeleteMapping("/{id}/grupos/{grupoId}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @Operation(summary = "Elimina un grupo y sus productos asociados (cascada)")
    public ComboDetalleResponse eliminarGrupo(@PathVariable Integer id, @PathVariable Integer grupoId) {
        comboService.eliminarGrupo(id, grupoId);
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    @PostMapping("/{id}/grupos/{grupoId}/productos")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agrega un producto a un grupo de un combo existente")
    public ComboDetalleResponse agregarProducto(@PathVariable Integer id, @PathVariable Integer grupoId,
                                                 @Valid @RequestBody ComboGrupoProductoRequest request) {
        comboService.agregarProducto(id, grupoId, request.productoId());
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    @DeleteMapping("/{id}/grupos/{grupoId}/productos/{productoId}")
    @PreAuthorize("hasPermission('productos_menu.combos', 'editar')")
    @Operation(summary = "Quita un producto de un grupo especifico de un combo")
    public ComboDetalleResponse quitarProducto(@PathVariable Integer id, @PathVariable Integer grupoId,
                                                @PathVariable Integer productoId) {
        comboService.quitarProducto(id, grupoId, productoId);
        return respuestaDetalle(comboService.buscarPorId(id));
    }

    private ComboDetalleResponse respuestaDetalle(Combo combo) {
        List<ComboGrupoDetalle> grupos = comboService.gruposDe(combo.getId());
        return ComboDetalleResponse.de(combo, grupos);
    }

    private static List<ComboGrupoCrear> aGruposCrear(List<ComboGrupoRequest> grupos) {
        if (grupos == null) {
            return null;
        }
        return grupos.stream().map(g -> new ComboGrupoCrear(g.nombre(), g.productosIds())).toList();
    }
}
