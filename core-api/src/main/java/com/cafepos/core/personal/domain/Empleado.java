package com.cafepos.core.personal.domain;

import com.cafepos.core.shared.texto.MascaraDocumento;
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

/** Mapea empleado (ver V1__schema_v4.sql, Modulo 8.1). */
@Entity
@Table(name = "empleado")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Empleado {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String cedula;

    @Column
    private String cargo;

    @Column
    private String telefono;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Empleado(Integer tenantId, String nombre, String cedula, String cargo, String telefono, String estado) {
        this.tenantId = tenantId;
        this.codigo = "";
        this.nombre = nombre;
        this.cedula = cedula;
        this.cargo = cargo;
        this.telefono = telefono;
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver EmpleadoService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCedulaEnmascarada() {
        return MascaraDocumento.enmascarar(cedula);
    }

    /** Actualizacion parcial (PATCH) — un campo en null significa "no tocar", salvo telefono (JsonNullable). */
    public void actualizar(String nombre, String cedula, String cargo, JsonNullable<String> telefono,
                            String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (cedula != null) {
            this.cedula = cedula;
        }
        if (cargo != null) {
            this.cargo = cargo;
        }
        if (telefono.isPresent()) {
            this.telefono = telefono.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
