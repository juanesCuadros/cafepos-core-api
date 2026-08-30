package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.application.EmpleadoService;
import com.cafepos.core.personal.domain.Empleado;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/empleados")
@Tag(name = ApiTags.PERSONAL)
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('personal.empleados', 'ver')")
    @Operation(summary = "Lista empleados, con filtros opcionales")
    public EmpleadosResponse listar(@RequestParam(required = false) String cargo,
                                     @RequestParam(required = false) String estado,
                                     @RequestParam(required = false) String q) {
        return EmpleadosResponse.de(empleadoService.listar(cargo, estado, q));
    }

    @PostMapping
    @PreAuthorize("hasPermission('personal.empleados', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un empleado", description = "409 si la cedula ya existe para el tenant.")
    public EmpleadoCreadoResponse crear(@Valid @RequestBody EmpleadoCrearRequest request) {
        Empleado empleado = empleadoService.crear(request.nombre(), request.cedula(), request.cargo(),
                request.telefono(), request.estado());
        return EmpleadoCreadoResponse.de(empleado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('personal.empleados', 'ver')")
    @Operation(summary = "Detalle completo de un empleado",
            description = "Incluye cedula SIN enmascarar, usuario_asociado (null si ninguno), resumen de turnos y "
                    + "propinas del mes calendario actual.")
    public EmpleadoDetalleResponse obtener(@PathVariable Integer id) {
        return EmpleadoDetalleResponse.de(empleadoService.detalle(id));
    }

    @GetMapping("/{id}/propinas")
    @PreAuthorize("hasPermission('personal.empleados', 'ver')")
    @Operation(summary = "Propinas atribuidas a un empleado en un rango de fechas")
    public PropinasEmpleadoResponse propinas(@PathVariable Integer id,
                                              @RequestParam(required = false) LocalDate fechaInicio,
                                              @RequestParam(required = false) LocalDate fechaFin) {
        return PropinasEmpleadoResponse.de(empleadoService.propinas(id, fechaInicio, fechaFin));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('personal.empleados', 'editar')")
    @Operation(summary = "Actualiza un empleado")
    public EmpleadoCreadoResponse actualizar(@PathVariable Integer id,
                                              @RequestBody EmpleadoActualizarRequest request) {
        Empleado empleado = empleadoService.actualizar(id, request.nombre(), request.cedula(), request.cargo(),
                request.telefono(), request.estado());
        return EmpleadoCreadoResponse.de(empleado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('personal.empleados', 'eliminar')")
    @Operation(summary = "Elimina un empleado (borrado fisico)")
    public EmpleadoEliminadoResponse eliminar(@PathVariable Integer id) {
        empleadoService.eliminar(id);
        return EmpleadoEliminadoResponse.INSTANCIA;
    }
}
