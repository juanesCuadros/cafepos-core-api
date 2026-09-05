package com.cafepos.core.restaurante.domain;

import java.time.LocalDate;

/**
 * Vista de solo lectura combinando facturacion_dian_resolucion (la
 * resolucion vigente mas reciente) con configuracion_sistema.estado_conexion_dian
 * (ver FacturacionDianRepository.buscarEstadoConexion — ese campo
 * pertenece conceptualmente al futuro Modulo 11-Configuracion, todavia no
 * existe como modulo propio). NUNCA incluye client_id_factus ni
 * client_secret_factus.
 */
public record FacturacionDianEstado(
        boolean configurada,
        String estadoConexion,
        String prefijo,
        Long rangoInicio,
        Long rangoFin,
        Long numeracionActual,
        LocalDate fechaExpedicion,
        LocalDate fechaVencimiento,
        String ambiente,
        String estado,
        Long numberingRangeId) {

    public static FacturacionDianEstado noConfigurada() {
        return new FacturacionDianEstado(false, "inactiva", null, null, null, null, null, null, null, null, null);
    }

    public static FacturacionDianEstado de(ResolucionVigenteResumen r, String estadoConexion) {
        return new FacturacionDianEstado(true, estadoConexion, r.prefijo(), r.rangoInicio(), r.rangoFin(),
                r.numeracionActual(), r.fechaExpedicion(), r.fechaVencimiento(), r.ambiente(), r.estado(),
                r.numberingRangeId());
    }
}
