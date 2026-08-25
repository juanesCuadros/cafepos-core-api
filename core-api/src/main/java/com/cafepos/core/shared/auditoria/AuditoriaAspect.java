package com.cafepos.core.shared.auditoria;

import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import com.cafepos.core.shared.seguridad.Permiso;
import com.cafepos.core.shared.seguridad.PermisoRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * DEBE ejecutar DENTRO de la misma transaccion que el metodo @Auditable (que
 * ya tiene su propio @Transactional) — nunca REQUIRES_NEW, a diferencia de
 * EventoSeguridadService (ver su Javadoc: ese caso SI necesita sobrevivir un
 * rollback). Si la transaccion hace rollback, el INSERT de este aspecto
 * tiene que desaparecer con ella.
 *
 * Esto depende del ORDEN de los advisors de Spring AOP: el advisor
 * transaccional (@Transactional) tiene que envolver a este aspecto por
 * FUERA (mas precedencia), para que este @Around corra DENTRO de la
 * transaccion ya abierta, no afuera de ella. Sin @Order explicito en
 * ninguno de los dos, ambos caen en Ordered.LOWEST_PRECEDENCE por defecto
 * — el orden relativo NO esta garantizado por el contrato de Spring en ese
 * caso. Verificado con una prueba real de rollback forzado (ver reporte de
 * implementacion en la conversacion que agrego esto): con un unico
 * @Aspect en todo el proyecto, el resultado observado fue el correcto
 * (INSERT revertido junto con la transaccion). Si se agrega otro @Aspect
 * al proyecto en el futuro, HAY QUE re-correr esa misma prueba antes de
 * confiar en el orden por defecto — no asumir que se mantiene.
 */
@Aspect
@Component
public class AuditoriaAspect {

    private final EventoAuditoriaRepository eventoAuditoriaRepository;
    private final PermisoRepository permisoRepository;
    private final ObjectMapper objectMapper;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public AuditoriaAspect(EventoAuditoriaRepository eventoAuditoriaRepository, PermisoRepository permisoRepository,
                            ObjectMapper objectMapper) {
        this.eventoAuditoriaRepository = eventoAuditoriaRepository;
        this.permisoRepository = permisoRepository;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(auditable)")
    public Object registrar(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        try {
            Object resultado = pjp.proceed();
            insertarEvento(pjp, auditable, resultado);
            return resultado;
        } finally {
            AuditoriaContext.limpiar();
        }
    }

    private void insertarEvento(ProceedingJoinPoint pjp, Auditable auditable, Object resultado) {
        Integer entidadId = evaluarEntidadId(pjp, auditable, resultado);
        /**
         * entidadTipo (concepto de negocio, ej. "venta") no es lo mismo que
         * permiso.modulo (namespace del catalogo RBAC, ej.
         * "caja.historial_ventas") — no hay mapeo generico confiable entre
         * ambos sin una config extra que @Auditable no tiene todavia. Se
         * intenta la busqueda igual (funciona en el caso raro donde
         * coinciden) y se deja permiso_id NULL cuando no matchea, en vez de
         * inventar una tabla de mapeo para esta prueba de concepto de un
         * solo caso (ver caso real: "venta"+"anular" no matchea
         * "caja.historial_ventas"+"anular", permiso_id queda NULL a
         * proposito, no es un bug).
         */
        Integer permisoId = permisoRepository.findByModuloAndAccion(auditable.entidadTipo(), auditable.accion())
                .map(Permiso::getId)
                .orElse(null);
        /** registrarDespues explicito gana sobre el valor de retorno — ver caso ConfiguracionRolService.actualizarPermisos. */
        JsonNode datosDespues = AuditoriaContext.obtenerDespues() != null
                ? AuditoriaContext.obtenerDespues()
                : objectMapper.valueToTree(resultado);

        EventoAuditoria evento = new EventoAuditoria(TenantContext.getCurrentTenantId(), usuarioActualId(),
                AuditoriaContext.obtenerUsuarioAutoriza(), permisoId, auditable.entidadTipo(), entidadId,
                auditable.accion(), AuditoriaContext.obtenerAntes(), datosDespues, ipOrigenActual(),
                userAgentActual());
        eventoAuditoriaRepository.save(evento);
    }

    /**
     * "#result" disponible en la expresion ademas de los argumentos —
     * necesario cuando el id de la entidad auditada no es un argumento del
     * metodo sino parte de lo que devuelve (ej. CajaJornadaService.egreso,
     * el id de jornada no es parametro; DevolucionService.solicitar, el id
     * de la devolucion se genera adentro). Mismo patron que "#result" en
     * @CacheEvict de Spring.
     */
    private Integer evaluarEntidadId(ProceedingJoinPoint pjp, Auditable auditable, Object resultado) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method metodo = signature.getMethod();
        StandardEvaluationContext contexto = new MethodBasedEvaluationContext(pjp.getTarget(), metodo, pjp.getArgs(),
                parameterNameDiscoverer);
        contexto.setVariable("result", resultado);
        Expression expression = expressionParser.parseExpression(auditable.entidadIdExpression());
        return expression.getValue(contexto, Integer.class);
    }

    private Integer usuarioActualId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUsuario usuario) {
            return usuario.usuarioId();
        }
        return null;
    }

    /** No critico (ver Auditable) — null si no hay request HTTP en curso en este hilo. */
    private String ipOrigenActual() {
        var request = requestActual();
        return request == null ? null : request.getRemoteAddr();
    }

    private String userAgentActual() {
        var request = requestActual();
        return request == null ? null : request.getHeader("User-Agent");
    }

    private jakarta.servlet.http.HttpServletRequest requestActual() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        return attrs.getRequest();
    }
}
