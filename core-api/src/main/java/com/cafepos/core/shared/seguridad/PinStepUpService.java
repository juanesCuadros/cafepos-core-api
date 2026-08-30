package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

/**
 * Reutilizable por cualquier endpoint que mute datos marcados con
 * requiere_pin=true en tenant_permiso_config (ver AjusteController para el
 * primer caso conectado). Llamar SIEMPRE al inicio del metodo del
 * controller/service, antes de ejecutar la mutacion, con el modulo/accion
 * de ese endpoint y el recurso_tipo/recurso_id de la peticion especifica
 * que se esta autorizando.
 */
@Service
public class PinStepUpService {

    private final JwtService jwtService;
    private final PermisoRepository permisoRepository;

    public PinStepUpService(JwtService jwtService, PermisoRepository permisoRepository) {
        this.jwtService = jwtService;
        this.permisoRepository = permisoRepository;
    }

    /**
     * pinTokenHeaderValue es el valor crudo del header X-Pin-Token. Cualquier
     * chequeo que falle (header ausente, JWT invalido/vencido, typ distinto
     * de "pin_stepup", tenant_id que no coincide con TenantContext, o
     * permiso_id/recurso_tipo/recurso_id que no coinciden EXACTO con lo
     * pedido) termina en la misma PinRequeridoException — nunca revelar cual
     * de los chequeos fallo especificamente.
     */
    public void validar(String pinTokenHeaderValue, String modulo, String accion, String recursoTipo,
                         Integer recursoId) {
        if (pinTokenHeaderValue == null || pinTokenHeaderValue.isBlank()) {
            throw new PinRequeridoException();
        }

        Claims claims;
        try {
            claims = jwtService.parseClaims(pinTokenHeaderValue);
        } catch (JwtException | IllegalArgumentException e) {
            throw new PinRequeridoException();
        }

        if (!jwtService.esPinStepUp(claims)) {
            throw new PinRequeridoException();
        }

        Integer tenantIdActual = TenantContext.getCurrentTenantId();
        if (tenantIdActual == null || !tenantIdActual.equals(jwtService.tenantId(claims))) {
            throw new PinRequeridoException();
        }

        Permiso permiso = permisoRepository.findByModuloAndAccion(modulo, accion)
                .orElseThrow(PinRequeridoException::new);

        if (!permiso.getId().equals(jwtService.permisoId(claims))
                || !recursoTipo.equals(jwtService.recursoTipo(claims))
                || !recursoId.equals(jwtService.recursoId(claims))) {
            throw new PinRequeridoException();
        }

        // Mismo "sub" del pin_token ya parseado arriba — quien autorizo con su PIN,
        // no necesariamente el usuario autenticado actual (ver PinVerificarService).
        // AuditoriaAspect lo lee si el metodo que termina llamando esta @Auditable.
        AuditoriaContext.registrarAutorizacion(jwtService.usuarioId(claims));
    }
}
