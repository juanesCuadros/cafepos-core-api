package com.cafepos.admin.negocios.infrastructure.web;

import com.cafepos.admin.negocios.application.CrearNegocioService;
import com.cafepos.admin.negocios.application.DetalleNegocioService;
import com.cafepos.admin.negocios.application.FacturacionDianAdminService;
import com.cafepos.admin.negocios.application.GestionTenantService;
import com.cafepos.admin.negocios.application.ListarNegociosService;
import com.cafepos.admin.negocios.application.NegocioCreado;
import com.cafepos.admin.negocios.application.VencimientoPruebaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/negocios")
@Tag(name = "Negocios")
public class NegocioController {

    private final CrearNegocioService crearNegocioService;
    private final ListarNegociosService listarNegociosService;
    private final DetalleNegocioService detalleNegocioService;
    private final GestionTenantService gestionTenantService;
    private final VencimientoPruebaService vencimientoPruebaService;
    private final FacturacionDianAdminService facturacionDianAdminService;
    private final String tenantBaseDomain;

    public NegocioController(CrearNegocioService crearNegocioService,
                             ListarNegociosService listarNegociosService,
                             DetalleNegocioService detalleNegocioService,
                             GestionTenantService gestionTenantService,
                             VencimientoPruebaService vencimientoPruebaService,
                             FacturacionDianAdminService facturacionDianAdminService,
                             @Value("${cafepos.tenant.base-domain}") String tenantBaseDomain) {
        this.crearNegocioService = crearNegocioService;
        this.listarNegociosService = listarNegociosService;
        this.detalleNegocioService = detalleNegocioService;
        this.gestionTenantService = gestionTenantService;
        this.vencimientoPruebaService = vencimientoPruebaService;
        this.facturacionDianAdminService = facturacionDianAdminService;
        this.tenantBaseDomain = tenantBaseDomain;
    }

    @GetMapping
    @Operation(summary = "Lista los negocios con paginación y filtros",
            description = "Devuelve listado paginado de negocios/tenants. Permite filtrar por texto (slug o nombre), por estado y por plan.")
    public Page<NegocioResumenResponse> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer planId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return listarNegociosService.ejecutar(q, estado, planId, pageable);
    }

    @GetMapping("/{tenantId}")
    @Operation(summary = "Ficha detallada 360 del negocio",
            description = "Devuelve toda la información del tenant, restaurante, plan contratado, estado DIAN y métricas básicas de usuarios.")
    public NegocioDetalleResponse detalle(@PathVariable Integer tenantId) {
        return detalleNegocioService.ejecutar(tenantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Da de alta un negocio nuevo",
            description = "Crea el tenant, el negocio, el primer usuario (Jefe, con contraseña temporal) y toda la configuración por defecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Negocio creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Falta el access token o no es válido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un negocio con ese slug")
    })
    public NegocioResponse crear(@Valid @RequestBody NegocioRequest request, Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        NegocioCreado creado = crearNegocioService.ejecutar(
                request.nombreNegocio(), request.slug(), request.planId(), request.correoJefe(), request.nombreJefe(), superadminId);
        String urlCompleta = "https://" + creado.slug() + "." + tenantBaseDomain;
        return new NegocioResponse(creado.tenantId(), creado.slug(), urlCompleta, creado.correoJefe(), creado.passwordTemporal());
    }

    @PutMapping("/{tenantId}")
    @Operation(summary = "Edita los datos comerciales del restaurante",
            description = "Actualiza nombre, NIT, dirección, ciudad, teléfono y correo de contacto del restaurante.")
    public Map<String, Object> editar(@PathVariable Integer tenantId,
                                      @Valid @RequestBody EditarNegocioRequest request,
                                      Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.editarRestaurante(tenantId, request, superadminId);
        return Map.of("mensaje", "Información del restaurante actualizada exitosamente", "tenant_id", tenantId);
    }

    @PostMapping("/{tenantId}/suspender")
    @Operation(summary = "Suspende manualmente un negocio",
            description = "Bloquea el acceso a todas las operaciones del negocio en core-api, registrando motivo de suspensión.")
    public Map<String, Object> suspender(@PathVariable Integer tenantId,
                                         @Valid @RequestBody CambiarEstadoTenantRequest request,
                                         Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.suspender(tenantId, request.motivo(), superadminId);
        return Map.of("mensaje", "Negocio suspendido correctamente", "tenant_id", tenantId);
    }

    @PostMapping("/{tenantId}/reactivar")
    @Operation(summary = "Reactiva un negocio suspendido o cancelado",
            description = "Restaura el estado a 'activo' y actualiza la fecha de próxima facturación.")
    public Map<String, Object> reactivar(@PathVariable Integer tenantId,
                                         @Valid @RequestBody CambiarEstadoTenantRequest request,
                                         Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.reactivar(tenantId, request.proximaFacturacion(), request.motivo(), superadminId);
        return Map.of("mensaje", "Negocio reactivado correctamente", "tenant_id", tenantId);
    }

    @PostMapping("/{tenantId}/cancelar")
    @Operation(summary = "Cancela definitivamente un negocio",
            description = "Pone el negocio en estado 'cancelado' de forma administrativa.")
    public Map<String, Object> cancelar(@PathVariable Integer tenantId,
                                        @Valid @RequestBody CambiarEstadoTenantRequest request,
                                        Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.cancelar(tenantId, request.motivo(), superadminId);
        return Map.of("mensaje", "Negocio cancelado correctamente", "tenant_id", tenantId);
    }

    @PostMapping("/{tenantId}/extender-prueba")
    @Operation(summary = "Extiende el período de prueba de un negocio",
            description = "Agrega días adicionales a la fecha de próxima facturación de un negocio en prueba.")
    public Map<String, Object> extenderPrueba(@PathVariable Integer tenantId,
                                              @Valid @RequestBody ExtenderPruebaRequest request,
                                              Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.extenderPrueba(tenantId, request.diasAdicionales(), request.motivo(), superadminId);
        return Map.of("mensaje", "Período de prueba extendido exitosamente", "tenant_id", tenantId);
    }

    @PutMapping("/{tenantId}/plan")
    @Operation(summary = "Cambia el plan de suscripción de un negocio",
            description = "Asigna un nuevo plan comercial registrando el historial de cambio.")
    public Map<String, Object> cambiarPlan(@PathVariable Integer tenantId,
                                           @Valid @RequestBody CambiarPlanTenantRequest request,
                                           Authentication authentication) {
        Integer superadminId = (Integer) authentication.getPrincipal();
        gestionTenantService.cambiarPlan(tenantId, request.nuevoPlanId(), request.motivo(), superadminId);
        return Map.of("mensaje", "Plan actualizado exitosamente", "tenant_id", tenantId, "nuevo_plan_id", request.nuevoPlanId());
    }

    @PostMapping("/vencimiento-prueba")
    @Operation(summary = "Ejecuta manualmente el vencimiento de pruebas",
            description = "Ejecuta el proceso que suspende los tenants en prueba cuya fecha de facturación ya pasó.")
    public VencimientoPruebaResponse vencimientoPrueba() {
        return VencimientoPruebaResponse.de(vencimientoPruebaService.ejecutar());
    }

    @PostMapping("/{tenantId}/facturacion-dian")
    @Operation(summary = "Configura las credenciales Factus de un negocio",
            description = "Guarda credenciales Factus cifradas con AES-256-GCM para el tenant.")
    public FacturacionDianResponse configurarFacturacionDian(
            @Parameter(description = "id del tenant a configurar") @PathVariable Integer tenantId,
            @Valid @RequestBody FacturacionDianRequest request) {
        facturacionDianAdminService.configurarCredenciales(tenantId, request.ambiente(), request.clientId(),
                request.clientSecret(), request.username(), request.password(), request.rangoInicio(),
                request.rangoFin(), request.prefijo(), request.fechaExpedicion(), request.fechaVencimiento(),
                request.numberingRangeId());
        return new FacturacionDianResponse(tenantId, true);
    }
}
