package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.application.ConfiguracionRolService;
import com.cafepos.core.configuracion.application.TiempoSesionService;
import com.cafepos.core.configuracion.domain.CambioPermiso;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@Tag(name = ApiTags.CONFIGURACION)
public class RolController {

    private final ConfiguracionRolService rolService;
    private final TiempoSesionService tiempoSesionService;

    public RolController(ConfiguracionRolService rolService, TiempoSesionService tiempoSesionService) {
        this.rolService = rolService;
        this.tiempoSesionService = tiempoSesionService;
    }

    /** Catalogo global de solo lectura, sin entrada propia en el catalogo de permisos — cualquier usuario autenticado lo puede ver. */
    @GetMapping
    @Operation(summary = "Lista los 5 roles fijos del sistema (catalogo global de solo lectura)")
    public RolesResponse listar() {
        return RolesResponse.de(rolService.listarRoles());
    }

    @GetMapping("/{id}/permisos")
    @PreAuthorize("hasPermission('configuracion.roles_permisos', 'ver')")
    @Operation(summary = "Matriz de permisos de un rol, agrupada por modulo padre")
    public MatrizRolPermisosResponse permisosDe(@PathVariable Integer id) {
        return MatrizRolPermisosResponse.de(rolService.matrizDe(id));
    }

    @PatchMapping("/{id}/permisos")
    @PreAuthorize("hasPermission('configuracion.roles_permisos', 'editar')")
    @Operation(summary = "Actualiza la matriz de permisos de un rol — el rol Jefe siempre responde 403")
    public PermisosActualizadosResponse actualizarPermisos(@PathVariable Integer id,
                                                             @Valid @RequestBody PermisosActualizarRequest request) {
        List<CambioPermiso> cambios = request.permisos().stream()
                .map(p -> new CambioPermiso(p.permisoId(), p.activo()))
                .toList();
        int aplicadoAUsuarios = rolService.actualizarPermisos(id, cambios);
        return PermisosActualizadosResponse.de(aplicadoAUsuarios);
    }

    @GetMapping("/tiempos-sesion")
    @PreAuthorize("hasPermission('configuracion.sistema', 'ver')")
    @Operation(summary = "Tiempos de inactividad de sesion configurados por rol")
    public TiemposSesionResponse tiemposSesion() {
        return TiemposSesionResponse.de(tiempoSesionService.listar());
    }

    @PatchMapping("/{id}/tiempo-sesion")
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Actualiza el tiempo de inactividad de sesion de un rol")
    public TiempoSesionActualizadoResponse actualizarTiempoSesion(@PathVariable Integer id,
            @Valid @RequestBody TiempoSesionActualizarRequest request) {
        return TiempoSesionActualizadoResponse.de(tiempoSesionService.actualizar(id, request.minutosInactividad()));
    }
}
