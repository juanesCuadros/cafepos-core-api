package com.cafepos.core.shared.auditoria;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * ThreadLocal liviano que conecta dos puntos que AuditoriaAspect no puede
 * ver por si solo:
 *
 * 1. El estado "antes" de la entidad que un metodo @Auditable esta por
 *    mutar — el metodo anotado llama registrarAntes(entidad) UNA VEZ, al
 *    principio, ANTES de mutarla. Se serializa a JSON de inmediato (no se
 *    guarda la referencia viva): la entidad JPA se muta en el mismo objeto
 *    en memoria (ver Venta.anular()), asi que guardar la referencia daria
 *    el mismo estado para "antes" y "despues".
 *
 * 2. Quien autorizo con PIN — PinStepUpService.validar() llama
 *    registrarAutorizacion(usuarioAutorizaId) apenas la validacion es
 *    exitosa (mismo JWT ya parseado ahi, no se inventa una fuente
 *    paralela). El usuario que autoriza con su PIN puede ser distinto del
 *    usuario autenticado que ejecuta la accion (ver PinVerificarService:
 *    el PIN se valida contra el correo indicado, no necesariamente el
 *    principal actual). Si el metodo @Auditable no tiene PIN real (ver
 *    caso ConfiguracionRolService.actualizarPermisos), esto simplemente
 *    queda null — no es un error, el ThreadLocal nunca se llena.
 *
 * 3. El estado "despues", SOLO para el caso donde el valor de retorno del
 *    metodo @Auditable no alcanza a representarlo (ej. un int plano que
 *    no muestra que permisos cambiaron) — registrarDespues(objeto) pisa
 *    lo que el aspecto usaria por defecto (el valor de retorno
 *    serializado). Opcional: si el metodo no la llama, el aspecto sigue
 *    usando el valor de retorno como siempre (caso ya probado de
 *    VentaService.anular no cambia).
 */
@Component
public class AuditoriaContext {

    private static final ThreadLocal<JsonNode> ANTES = new ThreadLocal<>();
    private static final ThreadLocal<JsonNode> DESPUES = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USUARIO_AUTORIZA_ID = new ThreadLocal<>();

    private static ObjectMapper objectMapper;

    public AuditoriaContext(ObjectMapper objectMapper) {
        AuditoriaContext.objectMapper = objectMapper;
    }

    public static void registrarAntes(Object entidad) {
        ANTES.set(objectMapper.valueToTree(entidad));
    }

    public static void registrarDespues(Object entidad) {
        DESPUES.set(objectMapper.valueToTree(entidad));
    }

    public static void registrarAutorizacion(Integer usuarioAutorizaId) {
        USUARIO_AUTORIZA_ID.set(usuarioAutorizaId);
    }

    static JsonNode obtenerAntes() {
        return ANTES.get();
    }

    static JsonNode obtenerDespues() {
        return DESPUES.get();
    }

    static Integer obtenerUsuarioAutoriza() {
        return USUARIO_AUTORIZA_ID.get();
    }

    static void limpiar() {
        ANTES.remove();
        DESPUES.remove();
        USUARIO_AUTORIZA_ID.remove();
    }
}
