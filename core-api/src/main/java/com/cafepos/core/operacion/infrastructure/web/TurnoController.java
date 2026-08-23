package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.TurnoService;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operacion/turno")
@Tag(name = ApiTags.OPERACION)
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping("/actual")
    @PreAuthorize("hasPermission('operacion.turno', 'ver')")
    @Operation(summary = "Si el usuario logueado tiene un turno activo ahora mismo")
    public TurnoActualResponse actual(Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return TurnoActualResponse.de(turnoService.actual(principal.usuarioId()));
    }

    @PostMapping("/iniciar")
    @PreAuthorize("hasPermission('operacion.turno', 'iniciar')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Inicia el turno del usuario logueado")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "El usuario no tiene un empleado asociado"),
            @ApiResponse(responseCode = "409", description = "Ya tiene un turno activo")
    })
    public TurnoIniciadoResponse iniciar(Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return TurnoIniciadoResponse.de(turnoService.iniciar(principal.usuarioId()));
    }

    @PostMapping("/cerrar")
    @PreAuthorize("hasPermission('operacion.turno', 'cerrar')")
    @Operation(summary = "Cierra el turno activo del usuario logueado")
    @ApiResponses({@ApiResponse(responseCode = "409", description = "No tiene un turno activo para cerrar")})
    public TurnoCerradoResponse cerrar(Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return TurnoCerradoResponse.de(turnoService.cerrar(principal.usuarioId()));
    }
}
