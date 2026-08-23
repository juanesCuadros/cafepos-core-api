package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.application.InsumoService;
import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.ResultadoEliminacionInsumo;
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

/**
 * "Existencias" e "Insumos" son la misma entidad (ver api_05_inventario.md,
 * nota de diseño) — este controller alimenta ambas vistas. RESOLUCION DE
 * PERMISOS: GET/POST/PATCH/DELETE aca usan inventario.insumos, NO
 * inventario.existencias (ese modulo solo cubre 'ver' + 'ajustar', ver
 * AjusteController) — son permisos distintos aunque compartan la tabla.
 */
@RestController
@RequestMapping("/insumos")
@Tag(name = ApiTags.INVENTARIO)
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('inventario.insumos', 'ver')")
    @Operation(summary = "Lista los insumos del tenant actual, con filtros opcionales")
    public InsumosResponse listar(@RequestParam(name = "categoria_insumo_id", required = false) Integer categoriaInsumoId,
                                   @RequestParam(required = false) String estado,
                                   @RequestParam(name = "estado_stock", required = false) String estadoStock,
                                   @RequestParam(required = false) String q) {
        return InsumosResponse.de(insumoService.listar(categoriaInsumoId, estado, estadoStock, q));
    }

    @PostMapping
    @PreAuthorize("hasPermission('inventario.insumos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un insumo — siempre nace con stock_actual y costo_actual en 0")
    public InsumoCreadoResponse crear(@Valid @RequestBody InsumoCrearRequest request) {
        Insumo insumo = insumoService.crear(request.nombre(), request.categoriaInsumoId(), request.unidadMedida(),
                request.stockMinimo(), request.stockMaximo(), request.fechaVencimRef(), request.estado());
        return InsumoCreadoResponse.de(insumo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('inventario.insumos', 'ver')")
    @Operation(summary = "Detalle completo de un insumo")
    public InsumoDetalleResponse obtener(@PathVariable Integer id) {
        return InsumoDetalleResponse.de(insumoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('inventario.insumos', 'editar')")
    @Operation(summary = "Actualiza los campos enviados de un insumo existente")
    public InsumoCreadoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody InsumoActualizarRequest request) {
        Insumo insumo = insumoService.actualizar(id, request.nombre(), request.categoriaInsumoId(),
                request.unidadMedida(), request.stockMinimo(), request.stockMaximo(), request.fechaVencimRef(),
                request.estado());
        return InsumoCreadoResponse.de(insumo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('inventario.insumos', 'eliminar')")
    @Operation(summary = "Elimina un insumo sin movimientos asociados, o lo marca inactivo si ya tiene movimientos")
    public InsumoEliminadoResponse eliminar(@PathVariable Integer id) {
        ResultadoEliminacionInsumo resultado = insumoService.eliminar(id);
        return resultado == ResultadoEliminacionInsumo.MARCADO_INACTIVO
                ? InsumoEliminadoResponse.marcadoInactivo()
                : InsumoEliminadoResponse.ELIMINADO;
    }
}
