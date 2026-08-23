package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.application.ImpresoraService;
import com.cafepos.core.configuracion.domain.Impresora;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/impresoras")
@Tag(name = ApiTags.CONFIGURACION)
public class ImpresoraController {

    private final ImpresoraService impresoraService;

    public ImpresoraController(ImpresoraService impresoraService) {
        this.impresoraService = impresoraService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'ver')")
    @Operation(summary = "Lista las impresoras del tenant actual")
    public ImpresorasResponse listar() {
        return ImpresorasResponse.de(impresoraService.listar());
    }

    @PostMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una impresora — tipo_conexion=ip exige ip+puerto, usb exige que ambos vengan nulos")
    public ImpresoraResponse crear(@Valid @RequestBody ImpresoraCrearRequest request) {
        Impresora impresora = impresoraService.crear(request.areaCocinaId(), request.tipo(), request.nombre(),
                request.tipoConexion(), request.ip(), request.puerto());
        return ImpresoraResponse.de(impresora);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Actualiza una impresora")
    public ImpresoraResponse actualizar(@PathVariable Integer id, @RequestBody ImpresoraActualizarRequest request) {
        Impresora impresora = impresoraService.actualizar(id, request.areaCocinaId(), request.tipo(),
                request.nombre(), request.tipoConexion(), request.ip(), request.puerto());
        return ImpresoraResponse.de(impresora);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Elimina una impresora")
    public ImpresoraEliminadaResponse eliminar(@PathVariable Integer id) {
        impresoraService.eliminar(id);
        return ImpresoraEliminadaResponse.ELIMINADA;
    }

    /**
     * "conectado":false es un resultado de negocio esperado (no hay
     * impresora real en desarrollo), no una excepcion — se decide el
     * status code directo aca en vez de pasar por el exception handler.
     */
    @PostMapping("/{id}/probar-conexion")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Intenta una conexion TCP real contra la impresora (solo tipo_conexion=ip)")
    public ResponseEntity<?> probarConexion(@PathVariable Integer id) {
        boolean conectado = impresoraService.probarConexion(id);
        if (conectado) {
            return ResponseEntity.ok(ProbarConexionResponse.EXITOSA);
        }
        return ResponseEntity.badRequest().body(ProbarConexionErrorResponse.FALLIDA);
    }
}
