package com.cafepos.admin.planes.infrastructure.web;

import com.cafepos.admin.planes.application.CambiarEstadoPlanService;
import com.cafepos.admin.planes.application.CrearPlanService;
import com.cafepos.admin.planes.application.EditarPlanService;
import com.cafepos.admin.planes.application.ListarPlanesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/planes")
@Tag(name = "Planes")
public class PlanController {

    private final CrearPlanService crearPlanService;
    private final ListarPlanesService listarPlanesService;
    private final EditarPlanService editarPlanService;
    private final CambiarEstadoPlanService cambiarEstadoPlanService;

    public PlanController(CrearPlanService crearPlanService,
                           ListarPlanesService listarPlanesService,
                           EditarPlanService editarPlanService,
                           CambiarEstadoPlanService cambiarEstadoPlanService) {
        this.crearPlanService = crearPlanService;
        this.listarPlanesService = listarPlanesService;
        this.editarPlanService = editarPlanService;
        this.cambiarEstadoPlanService = cambiarEstadoPlanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un plan", description = "Crea un plan de suscripción nuevo. Requiere JWT de Super Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plan creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido")
    })
    public PlanResponse crear(@Valid @RequestBody PlanRequest request) {
        return PlanResponse.de(crearPlanService.ejecutar(request.nombre(), request.descripcion(),
                request.precioMensual(), request.limiteUsuarios(), request.diasPrueba()));
    }

    @GetMapping
    @Operation(summary = "Lista los planes", description = "Devuelve todos los planes, activos e inactivos — "
            + "el Super Admin necesita ver ambos para gestionarlos. Requiere JWT de Super Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de planes"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido")
    })
    public List<PlanResponse> listar() {
        return listarPlanesService.ejecutar().stream().map(PlanResponse::de).toList();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un plan", description = "Reemplaza los datos de un plan existente. Requiere JWT de Super Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plan editado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido"),
            @ApiResponse(responseCode = "404", description = "El plan no existe")
    })
    public PlanResponse editar(@PathVariable Integer id, @Valid @RequestBody PlanRequest request) {
        return PlanResponse.de(editarPlanService.ejecutar(id, request.nombre(), request.descripcion(),
                request.precioMensual(), request.limiteUsuarios(), request.diasPrueba()));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activa o desactiva un plan", description = "Cambia el estado del plan entre 'activo' e "
            + "'inactivo'. Nunca borra el plan físicamente — mismo criterio de baja lógica que el resto del "
            + "sistema. Requiere JWT de Super Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "El estado debe ser 'activo' o 'inactivo'"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido"),
            @ApiResponse(responseCode = "404", description = "El plan no existe")
    })
    public PlanResponse cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoPlanRequest request) {
        return PlanResponse.de(cambiarEstadoPlanService.ejecutar(id, request.estado()));
    }
}
