package com.cafepos.admin.negocios.infrastructure.web;

import com.cafepos.admin.negocios.application.CrearNegocioService;
import com.cafepos.admin.negocios.application.NegocioCreado;
import com.cafepos.admin.negocios.application.VencimientoPruebaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/negocios")
@Tag(name = "Negocios")
public class NegocioController {

    private final CrearNegocioService crearNegocioService;
    private final VencimientoPruebaService vencimientoPruebaService;
    private final String tenantBaseDomain;

    public NegocioController(CrearNegocioService crearNegocioService,
                              VencimientoPruebaService vencimientoPruebaService,
                              @Value("${cafepos.tenant.base-domain}") String tenantBaseDomain) {
        this.crearNegocioService = crearNegocioService;
        this.vencimientoPruebaService = vencimientoPruebaService;
        this.tenantBaseDomain = tenantBaseDomain;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Da de alta un negocio nuevo",
            description = "Crea el tenant, el negocio, el primer usuario (Jefe, con contraseña "
                    + "temporal) y toda la configuración por defecto (permisos por rol, tiempos de "
                    + "sesión, método de pago Efectivo). Requiere un access token JWT de Super Admin "
                    + "válido. La contraseña temporal del Jefe se devuelve en texto plano SOLO en "
                    + "esta respuesta — no se puede volver a consultar después, ni queda registrada "
                    + "en ningún log; anotala antes de cerrar esta pantalla.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Negocio creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (slug con formato incorrecto, "
                    + "correo del Jefe mal formado, o el plan seleccionado no existe)"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un negocio con ese identificador (slug)")
    })
    public NegocioResponse crear(@Valid @RequestBody NegocioRequest request, Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        NegocioCreado creado = crearNegocioService.ejecutar(
                request.nombreNegocio(), request.slug(), request.planId(), request.correoJefe(), request.nombreJefe(), superadminId);
        String urlCompleta = "https://" + creado.slug() + "." + tenantBaseDomain;
        return new NegocioResponse(creado.tenantId(), creado.slug(), urlCompleta, creado.correoJefe(), creado.passwordTemporal());
    }

    @PostMapping("/vencimiento-prueba")
    @Operation(
            summary = "Ejecuta manualmente el vencimiento de pruebas",
            description = "Corre ahora mismo, a mano, el mismo proceso que ya corre solo todas las "
                    + "medianoches (VencimientoPruebaJob): suspende los tenants en período de prueba "
                    + "cuya fecha de próxima facturación ya pasó. Sirve para dos cosas — probarlo en "
                    + "desarrollo sin esperar al cron, y como herramienta operativa real: si el job "
                    + "automático falla una noche por lo que sea, se puede volver a correr desde acá "
                    + "en vez de esperar 24 horas a la próxima corrida. Requiere JWT de Super Admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejecución completa — candidatos evaluados, "
                    + "cuántos quedaron suspendidos, y sus slugs"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public VencimientoPruebaResponse vencimientoPrueba() {
        return VencimientoPruebaResponse.de(vencimientoPruebaService.ejecutar());
    }
}
