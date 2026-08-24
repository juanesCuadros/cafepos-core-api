package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.CajaJornadaService;
import com.cafepos.core.caja.domain.JornadaNoAbiertaException;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import com.cafepos.core.shared.seguridad.PinStepUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caja/jornada")
@Tag(name = ApiTags.CAJA)
public class CajaJornadaController {

    private static final String MODULO = "caja.apertura_cierre";
    private static final String ACCION_EGRESO = "registrar_egreso";
    private static final String RECURSO_TIPO_JORNADA = "caja_jornada";

    private final CajaJornadaService cajaJornadaService;
    private final PinStepUpService pinStepUpService;

    public CajaJornadaController(CajaJornadaService cajaJornadaService, PinStepUpService pinStepUpService) {
        this.cajaJornadaService = cajaJornadaService;
        this.pinStepUpService = pinStepUpService;
    }

    @GetMapping("/actual")
    @PreAuthorize("hasPermission('caja.apertura_cierre', 'ver')")
    @Operation(summary = "Estado de la jornada de caja abierta actual, si existe")
    public JornadaActualResponse actual() {
        return JornadaActualResponse.de(cajaJornadaService.actual());
    }

    @PostMapping("/abrir")
    @PreAuthorize("hasPermission('caja.apertura_cierre', 'abrir_caja')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abre una jornada de caja nueva")
    @ApiResponses({@ApiResponse(responseCode = "409", description = "Ya existe una jornada abierta (RN-011)")})
    public JornadaAbiertaResponse abrir(@Valid @RequestBody JornadaAbrirRequest request,
                                         Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return JornadaAbiertaResponse.de(cajaJornadaService.abrir(principal.usuarioId(), request.montoInicial()));
    }

    @PostMapping("/ingreso")
    @PreAuthorize("hasPermission('caja.apertura_cierre', 'registrar_ingreso')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un ingreso manual de caja")
    @ApiResponses({@ApiResponse(responseCode = "400", description = "No hay jornada abierta")})
    public MovimientoResponse ingreso(@Valid @RequestBody IngresoRequest request, Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return MovimientoResponse.de(
                cajaJornadaService.ingreso(principal.usuarioId(), request.monto(), request.motivo()));
    }

    @PostMapping("/egreso")
    @PreAuthorize("hasPermission('caja.apertura_cierre', 'registrar_egreso')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un egreso manual de caja",
            description = "Requiere PIN de step-up — header X-Pin-Token con el pin_token emitido por "
                    + "POST /auth/pin/verificar para modulo=caja.apertura_cierre, accion=registrar_egreso, "
                    + "recurso_tipo=caja_jornada, recurso_id=el id de la jornada abierta actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "No hay jornada abierta"),
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es valido")
    })
    public MovimientoResponse egreso(@Valid @RequestBody EgresoRequest request,
                                      @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                      @RequestHeader(name = "X-Pin-Token", required = false) String pinToken,
                                      Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        Integer jornadaId = cajaJornadaService.actual()
                .map(v -> v.jornada().getId())
                .orElseThrow(JornadaNoAbiertaException::new);
        pinStepUpService.validar(pinToken, MODULO, ACCION_EGRESO, RECURSO_TIPO_JORNADA, jornadaId);
        return MovimientoResponse.de(
                cajaJornadaService.egreso(principal.usuarioId(), request.monto(), request.motivo()));
    }

    @PostMapping("/cerrar")
    @PreAuthorize("hasPermission('caja.apertura_cierre', 'cerrar_caja')")
    @Operation(summary = "Cierra la jornada de caja actual (arqueo)",
            description = "diferencia NUNCA bloquea el cierre (RN-013).")
    @ApiResponses({@ApiResponse(responseCode = "400", description = "No hay jornada abierta")})
    public CerrarJornadaResponse cerrar(@Valid @RequestBody CerrarJornadaRequest request,
                                         Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return CerrarJornadaResponse.de(
                cajaJornadaService.cerrar(principal.usuarioId(), request.montoFinalFisico()));
    }
}
