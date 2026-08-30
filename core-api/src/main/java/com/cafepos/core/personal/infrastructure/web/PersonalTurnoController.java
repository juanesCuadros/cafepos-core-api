package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.application.TurnoService;
import com.cafepos.core.personal.domain.Turno;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import java.time.LocalDate;

/**
 * Gestion Admin/Jefe de turnos (8.2) — distinta del autoregistro del
 * empleado (POST /turnos/abrir en operacion.infrastructure.web.TurnoController,
 * misma tabla turno, pantalla distinta, ver api_02_operacion.md). Nombre
 * de clase con prefijo "Personal" a proposito — coincide en nombre simple
 * con operacion.infrastructure.web.TurnoController y Spring usa ese
 * nombre (en minuscula) como bean id por defecto, lo mismo para
 * PersonalTurnoJpaRepository/PersonalTurnoRepositoryAdapter — sin el
 * prefijo, ConflictingBeanDefinitionException al arrancar (confirmado real).
 */
@RestController
@RequestMapping("/turnos")
@Tag(name = ApiTags.PERSONAL)
public class PersonalTurnoController {

    private final TurnoService turnoService;

    public PersonalTurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('personal.turnos', 'ver')")
    @Operation(summary = "Lista turnos, con filtros opcionales")
    public TurnosResponse listar(@RequestParam(required = false) LocalDate fechaInicio,
                                  @RequestParam(required = false) LocalDate fechaFin,
                                  @RequestParam(required = false) Integer empleadoId) {
        return TurnosResponse.de(turnoService.listar(fechaInicio, fechaFin, empleadoId));
    }

    @PostMapping
    @PreAuthorize("hasPermission('personal.turnos', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un turno manual para un empleado",
            description = "usuario_id de la fila es quien registra (el admin/jefe autenticado), no el empleado. "
                    + "horas_trabajadas siempre se calcula, nunca se acepta del cliente.")
    public TurnoCreadoResponse crear(@Valid @RequestBody TurnoCrearRequest request, Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        Turno turno = turnoService.crear(request.empleadoId(), request.fecha(), request.horaInicio(),
                request.horaFin(), request.observaciones(), principal.usuarioId());
        return TurnoCreadoResponse.de(turno);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('personal.turnos', 'editar')")
    @Operation(summary = "Actualiza un turno", description = "Si cambia hora_inicio u hora_fin, horas_trabajadas se recalcula.")
    public TurnoCreadoResponse actualizar(@PathVariable Integer id, @RequestBody TurnoActualizarRequest request) {
        Turno turno = turnoService.actualizar(id, request.empleadoId(), request.fecha(), request.horaInicio(),
                request.horaFin(), request.observaciones());
        return TurnoCreadoResponse.de(turno);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('personal.turnos', 'eliminar')")
    @Operation(summary = "Elimina un turno")
    public TurnoEliminadoResponse eliminar(@PathVariable Integer id) {
        turnoService.eliminar(id);
        return TurnoEliminadoResponse.INSTANCIA;
    }
}
