package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

interface FacturacionDianJpaRepository extends TenantAwareRepository<FacturacionDianResolucion, Integer> {

    /**
     * Carga la entidad completa (con los 4 campos Factus cifrados) —
     * unico caller real: FacturacionDianRepositoryAdapter.buscarVigenteConCredenciales().
     * Para el resto, ver buscarVigenteResumen()/incrementarYReservarNumero()
     * de abajo, que nunca tocan esas columnas.
     */
    Optional<FacturacionDianResolucion> findTopByOrderByIdDesc();

    /**
     * estado_conexion_dian vive en configuracion_sistema (Modulo 11, todavia
     * no existe como modulo Java propio) — lectura acotada a esta unica
     * columna, no se mapea configuracion_sistema como entidad completa aca.
     * RLS ya scopea a la fila del tenant actual (UNIQUE(tenant_id)).
     */
    @Query(value = "SELECT estado_conexion_dian FROM configuracion_sistema LIMIT 1", nativeQuery = true)
    Optional<String> buscarEstadoConexionDian();

    /**
     * Proyeccion nativa sin las 4 columnas Factus cifradas — ni siquiera
     * las incluye en el SELECT, asi que @Convert nunca se dispara. Ver
     * Javadoc de ResolucionVigenteResumen.
     */
    @Query(value = """
            SELECT prefijo, rango_inicio AS rangoInicio, rango_fin AS rangoFin,
                   numeracion_actual AS numeracionActual, fecha_expedicion AS fechaExpedicion,
                   fecha_vencimiento AS fechaVencimiento, ambiente, estado,
                   numbering_range_id AS numberingRangeId
            FROM facturacion_dian_resolucion
            ORDER BY id DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<ResolucionVigenteResumenProjection> buscarVigenteResumen();

    /**
     * UPDATE atomico de una sola sentencia: encuentra la fila vigente
     * (subquery ORDER BY id DESC LIMIT 1, mismo criterio que
     * findTopByOrderByIdDesc/buscarVigenteResumen), incrementa
     * numeracion_actual, y devuelve el resultado via RETURNING — todo en
     * el mismo viaje a la base, sin cargar la entidad completa y sin la
     * condicion de carrera de un load-then-save. Nunca lee ni escribe las
     * columnas Factus cifradas: ni siquiera aparecen en esta sentencia.
     * Optional.empty() si el tenant no tiene ninguna fila (subquery da
     * NULL, el WHERE no matchea nada, 0 filas afectadas).
     */
    @Transactional
    @Query(value = """
            UPDATE facturacion_dian_resolucion
            SET numeracion_actual = COALESCE(numeracion_actual, 0) + 1,
                updated_at = now()
            WHERE id = (SELECT id FROM facturacion_dian_resolucion ORDER BY id DESC LIMIT 1)
            RETURNING id AS resolucionId, prefijo, numeracion_actual::int AS numero
            """, nativeQuery = true)
    Optional<NumeroFacturaReservadoProjection> incrementarYReservarNumero();

    interface ResolucionVigenteResumenProjection {
        String getPrefijo();
        Long getRangoInicio();
        Long getRangoFin();
        Long getNumeracionActual();
        LocalDate getFechaExpedicion();
        LocalDate getFechaVencimiento();
        String getAmbiente();
        String getEstado();
        Long getNumberingRangeId();
    }

    interface NumeroFacturaReservadoProjection {
        Integer getResolucionId();
        String getPrefijo();
        Integer getNumero();
    }
}
