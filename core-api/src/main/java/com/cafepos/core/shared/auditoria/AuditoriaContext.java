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
 *    principal actual).
 */
@Component
public class AuditoriaContext {

    private static final ThreadLocal<JsonNode> ANTES = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USUARIO_AUTORIZA_ID = new ThreadLocal<>();

    private static ObjectMapper objectMapper;

    public AuditoriaContext(ObjectMapper objectMapper) {
        AuditoriaContext.objectMapper = objectMapper;
    }

    public static void registrarAntes(Object entidad) {
        ANTES.set(objectMapper.valueToTree(entidad));
    }

    public static void registrarAutorizacion(Integer usuarioAutorizaId) {
        USUARIO_AUTORIZA_ID.set(usuarioAutorizaId);
    }

    static JsonNode obtenerAntes() {
        return ANTES.get();
    }

    static Integer obtenerUsuarioAutoriza() {
        return USUARIO_AUTORIZA_ID.get();
    }

    static void limpiar() {
        ANTES.remove();
        USUARIO_AUTORIZA_ID.remove();
    }
}
