package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.application.RestauranteService;
import com.cafepos.core.restaurante.domain.Restaurante;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurante")
@Tag(name = ApiTags.RESTAURANTE)
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('restaurante.info_general', 'ver')")
    @Operation(summary = "Informacion general del negocio (registro unico por tenant)")
    public RestauranteResponse obtener() {
        return RestauranteResponse.de(restauranteService.obtener());
    }

    @PatchMapping
    @PreAuthorize("hasPermission('restaurante.info_general', 'editar')")
    @Operation(summary = "Actualiza la informacion general del negocio")
    public RestauranteResponse actualizar(@Valid @RequestBody RestauranteActualizarRequest request) {
        Restaurante restaurante = restauranteService.actualizar(request.nombreNegocio(), request.nit(),
                request.direccion(), request.departamento(), request.ciudad(), request.pais(), request.telefono(),
                request.correo(), request.telefonoRepresentante(), request.logoUrl(), request.redesSociales(),
                request.descripcion());
        return RestauranteResponse.de(restaurante);
    }
}
