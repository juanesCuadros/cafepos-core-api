package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.application.ConfiguracionUsuarioService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.Usuario;
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
 * POST /usuarios/{id}/resetear-password NO esta implementado a proposito
 * (diferido) — depende de infraestructura de recuperacion de password
 * (proveedor de correo) que todavia no existe en el proyecto. No hay ni
 * siquiera un stub aca; se agrega cuando esa infraestructura exista.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(name = ApiTags.CONFIGURACION)
public class UsuarioController {

    private final ConfiguracionUsuarioService usuarioService;

    public UsuarioController(ConfiguracionUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('configuracion.usuarios', 'ver')")
    @Operation(summary = "Lista los usuarios del tenant actual, filtrable por rol_id y estado")
    public UsuariosResponse listar(@RequestParam(required = false) Integer rolId,
                                    @RequestParam(required = false) String estado) {
        return UsuariosResponse.de(usuarioService.listar(rolId, estado));
    }

    @PostMapping
    @PreAuthorize("hasPermission('configuracion.usuarios', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un usuario adicional en el tenant actual")
    public UsuarioCreadoResponse crear(@Valid @RequestBody UsuarioCrearRequest request) {
        Usuario usuario = usuarioService.crear(request.nombre(), request.correo(), request.password(),
                request.rolId(), request.empleadoId(), request.pin(), request.estado());
        return UsuarioCreadoResponse.de(usuario);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.usuarios', 'ver')")
    @Operation(summary = "Detalle de un usuario — nunca incluye password_hash ni pin_autorizacion_hash")
    public UsuarioDetalleResponse obtener(@PathVariable Integer id) {
        return UsuarioDetalleResponse.de(usuarioService.detalleDe(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.usuarios', 'editar')")
    @Operation(summary = "Actualiza un usuario — password nunca cambia por aca")
    public UsuarioCreadoResponse actualizar(@PathVariable Integer id, @RequestBody UsuarioActualizarRequest request) {
        Usuario usuario = usuarioService.actualizar(id, request.nombre(), request.correo(), request.rolId(),
                request.empleadoId(), request.pin(), request.estado());
        return UsuarioCreadoResponse.de(usuario);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('configuracion.usuarios', 'eliminar')")
    @Operation(summary = "Elimina un usuario — exclusivo Jefe segun el catalogo de permisos")
    public UsuarioEliminadoResponse eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return UsuarioEliminadoResponse.ELIMINADO;
    }
}
