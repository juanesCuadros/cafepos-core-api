package com.cafepos.core.restaurante.domain;

import java.time.LocalDate;

/**
 * Proyeccion de facturacion_dian_resolucion SIN las 4 columnas Factus
 * cifradas (client_id_factus, client_secret_factus, username_factus,
 * password_factus) — para callers que no necesitan las credenciales (ver
 * FacturacionDianRepository.buscarVigenteResumen). A diferencia de cargar
 * la entidad FacturacionDianResolucion completa, esta consulta nunca
 * dispara @Convert/decrypt de esos 4 campos: un ciphertext corrupto en
 * cualquiera de ellos no puede romper a un caller que solo necesitaba
 * prefijo/numeracion/vigencia.
 */
public record ResolucionVigenteResumen(
        String prefijo,
        Long rangoInicio,
        Long rangoFin,
        Long numeracionActual,
        LocalDate fechaExpedicion,
        LocalDate fechaVencimiento,
        String ambiente,
        String estado,
        Long numberingRangeId) {
}
