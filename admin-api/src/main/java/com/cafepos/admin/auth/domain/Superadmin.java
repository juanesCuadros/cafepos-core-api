package com.cafepos.admin.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Mapea directamente la tabla superadmin (creada por core-api, ver
 * V1__schema_v4.sql). No mapea totp_secret ni totp_habilitado a proposito:
 * 2FA queda fuera de alcance por ahora, no dejar campos a medio usar.
 */
@Entity
@Table(name = "superadmin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Superadmin {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Superadmin(String nombre, String correo, String passwordHash) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.estado = ESTADO_ACTIVO;
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }
}
