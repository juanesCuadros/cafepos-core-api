package com.cafepos.core.restaurante.domain;

import java.util.Optional;

/** Puerto de persistencia de FacturacionDianResolucion — implementado en infrastructure.persistence. */
public interface FacturacionDianRepository {

    /**
     * Fila 1-a-N vigente (la mas reciente) del tenant, SIN las 4 columnas
     * Factus cifradas — nunca dispara @Convert/decrypt al cargarse (ver
     * Javadoc de ResolucionVigenteResumen). Usar esta salvo que se
     * necesiten las credenciales reales descifradas (ver
     * buscarVigenteConCredenciales).
     */
    Optional<ResolucionVigenteResumen> buscarVigenteResumen();

    /**
     * Misma fila vigente que buscarVigenteResumen(), pero cargando la
     * entidad completa — UNICA razon real para esto es que
     * credencialesFactusPara() necesita las credenciales Factus
     * descifradas. Dispara @Convert/decrypt de los 4 campos Factus en la
     * carga (ver FacturacionDianResolucion) — no usar para nada que no sea
     * leer esas credenciales.
     */
    Optional<FacturacionDianResolucion> buscarVigenteConCredenciales();

    /**
     * estado_conexion_dian vive en configuracion_sistema, no en
     * facturacion_dian_resolucion — ver Javadoc de FacturacionDianEstado.
     */
    Optional<String> buscarEstadoConexion();

    /**
     * Incrementa numeracion_actual de la resolucion vigente con un UPDATE
     * atomico de una sola sentencia (RETURNING) — nunca lee ni escribe las
     * columnas Factus cifradas, y no tiene la condicion de carrera del
     * viejo patron load-entidad-completa + incrementarNumeracion() +
     * guardar() (dos ventas concurrentes podian leer el mismo valor base
     * antes de que cualquiera de las dos escribiera). Optional.empty() si
     * el tenant no tiene ninguna resolucion vigente.
     */
    Optional<NumeroFacturaReservado> incrementarYReservarNumero();

    /**
     * Persiste la entidad completa. Unico caller real hoy:
     * FacturacionDianResolucionEncriptacionIT (round-trip de cifrado) —
     * nada en produccion escribe esta entidad desde core-api, la escritura
     * real de credenciales/rango/ambiente la hace admin-api directo contra
     * la misma tabla (ver Javadoc de FacturacionDianResolucion).
     */
    FacturacionDianResolucion guardar(FacturacionDianResolucion resolucion);
}
