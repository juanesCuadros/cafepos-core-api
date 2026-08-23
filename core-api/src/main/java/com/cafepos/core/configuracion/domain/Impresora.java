package com.cafepos.core.configuracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.OffsetDateTime;

/**
 * Mapea impresora (ver V1__schema_v4.sql, Modulo 11.3). tipoConexion=ip
 * exige ip+puerto no nulos; tipoConexion=usb exige ambos nulos — validado
 * en el constructor y en actualizar(), nunca solo en el DTO de entrada.
 */
@Entity
@Table(name = "impresora")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Impresora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "area_cocina_id")
    private Integer areaCocinaId;

    @Column(nullable = false)
    private String tipo;

    @Column
    private String nombre;

    @Column(name = "tipo_conexion", nullable = false)
    private String tipoConexion;

    @Column
    private String ip;

    @Column
    private Integer puerto;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Impresora(Integer tenantId, Integer areaCocinaId, String tipo, String nombre, String tipoConexion,
                      String ip, Integer puerto) {
        validarConexion(tipoConexion, ip, puerto);
        this.tenantId = tenantId;
        this.areaCocinaId = areaCocinaId;
        this.tipo = tipo;
        this.nombre = nombre;
        this.tipoConexion = tipoConexion;
        this.ip = ip;
        this.puerto = puerto;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * ip/puerto son JsonNullable a proposito (aunque tipoConexion no) —
     * pasar a tipo_conexion=usb requiere poder mandar ip/puerto en null
     * explicito para limpiar una conexion ip previa, no alcanza con
     * omitirlos (eso significaria "no tocar" y dejaria el valor viejo,
     * lo que rompe la validacion de abajo).
     */
    public void actualizar(JsonNullable<Integer> areaCocinaId, String tipo, String nombre, String tipoConexion,
                            JsonNullable<String> ip, JsonNullable<Integer> puerto) {
        String tipoConexionFinal = tipoConexion != null ? tipoConexion : this.tipoConexion;
        String ipFinal = ip.isPresent() ? ip.get() : this.ip;
        Integer puertoFinal = puerto.isPresent() ? puerto.get() : this.puerto;
        validarConexion(tipoConexionFinal, ipFinal, puertoFinal);
        if (areaCocinaId.isPresent()) {
            this.areaCocinaId = areaCocinaId.get();
        }
        if (tipo != null) {
            this.tipo = tipo;
        }
        if (nombre != null) {
            this.nombre = nombre;
        }
        this.tipoConexion = tipoConexionFinal;
        this.ip = ipFinal;
        this.puerto = puertoFinal;
        this.updatedAt = OffsetDateTime.now();
    }

    private static void validarConexion(String tipoConexion, String ip, Integer puerto) {
        if ("ip".equals(tipoConexion) && (ip == null || puerto == null)) {
            throw new ImpresoraConexionInvalidaException("Una impresora con tipo_conexion=ip requiere ip y puerto");
        }
        if ("usb".equals(tipoConexion) && (ip != null || puerto != null)) {
            throw new ImpresoraConexionInvalidaException("Una impresora con tipo_conexion=usb no debe tener ip ni puerto");
        }
    }
}
