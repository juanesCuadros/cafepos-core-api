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
import java.time.ZoneOffset;

/**
 * Mapea directamente la tabla superadmin (creada por core-api, ver
 * V1__schema_v4.sql y V30__superadmin_seguridad_bloqueo_auditoria.sql).
 */
@Entity
@Table(name = "superadmin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Superadmin {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";
    public static final int MAX_INTENTOS_FALLIDOS = 5;
    public static final int MINUTOS_BLOQUEO = 30;

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

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Superadmin(String nombre, String correo, String passwordHash) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.estado = ESTADO_ACTIVO;
        this.intentosFallidos = 0;
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public boolean estaBloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= MAX_INTENTOS_FALLIDOS) {
            this.bloqueadoHasta = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(MINUTOS_BLOQUEO);
        }
    }

    public void resetearIntentosFallidos() {
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
    }

    public void cambiarPassword(String nuevoPasswordHash) {
        this.passwordHash = nuevoPasswordHash;
    }

    public void actualizarPerfil(String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre;
        }
    }
}
