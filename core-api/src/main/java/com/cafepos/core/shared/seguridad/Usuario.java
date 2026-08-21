package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea usuario (ver V1__schema_v4.sql). Solo lectura/actualizacion — la creacion la hace admin-api. */
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    private static final String ESTADO_ACTIVO = "activo";

    @Id
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String estado;

    @Column(name = "debe_cambiar_password", nullable = false)
    private boolean debeCambiarPassword;

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public void cambiarPassword(String nuevoPasswordHash) {
        this.passwordHash = nuevoPasswordHash;
        this.debeCambiarPassword = false;
    }
}
