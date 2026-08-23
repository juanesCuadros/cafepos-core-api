package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.application.ConfiguracionSistemaService;
import com.cafepos.core.configuracion.domain.ConfiguracionSistema;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configuracion-sistema")
@Tag(name = ApiTags.CONFIGURACION)
public class ConfiguracionSistemaController {

    private final ConfiguracionSistemaService configuracionSistemaService;

    public ConfiguracionSistemaController(ConfiguracionSistemaService configuracionSistemaService) {
        this.configuracionSistemaService = configuracionSistemaService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'ver')")
    @Operation(summary = "Configuracion general del sistema (registro unico por tenant)")
    public ConfiguracionSistemaResponse obtener() {
        return ConfiguracionSistemaResponse.de(configuracionSistemaService.obtener());
    }

    @PatchMapping
    @PreAuthorize("hasPermission('configuracion.sistema', 'editar')")
    @Operation(summary = "Actualiza la configuracion general del sistema")
    public ConfiguracionSistemaResponse actualizar(@RequestBody ConfiguracionSistemaActualizarRequest request) {
        ConfiguracionSistema c = configuracionSistemaService.actualizar(request.modoComanda(),
                request.tiempoLimitePrepMin(), request.propinaTipo(), request.propinaPorcentaje(),
                request.propinaDestino(), request.propinaPctMesero(), request.diasAnticipacionVencim(),
                request.estadoConexionDian(), request.ivaPorcentaje(), request.incPorcentaje());
        return ConfiguracionSistemaResponse.de(c);
    }
}
