package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/** Mapea usuario (ver V1__schema_v4.sql). Solo lectura/actualizacion — la creacion la hace admin-api. */
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    private static final String ESTADO_ACTIVO = "activo";

    /** RN-008: 5 intentos fallidos consecutivos bloquean el login por 15 minutos. */
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final long BLOQUEO_MINUTOS = 15;

    @Id
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", insertable = false, updatable = false)
    private Rol rol;

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

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public void cambiarPassword(String nuevoPasswordHash) {
        this.passwordHash = nuevoPasswordHash;
        this.debeCambiarPassword = false;
    }

    public boolean estaBloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(OffsetDateTime.now());
    }

    /** Al llegar a MAX_INTENTOS_FALLIDOS, bloquea y resetea el contador para que el proximo ciclo cuente desde cero. */
    public void registrarIntentoFallido() {
        intentosFallidos++;
        if (intentosFallidos >= MAX_INTENTOS_FALLIDOS) {
            bloqueadoHasta = OffsetDateTime.now().plusMinutes(BLOQUEO_MINUTOS);
            intentosFallidos = 0;
        }
    }

    /**
     * No toca bloqueadoHasta: si ya vencio, estaBloqueado() ya da false por
     * la comparacion contra now(), no hace falta limpiarlo aca.
     */
    public void registrarLoginExitoso() {
        intentosFallidos = 0;
    }
}
