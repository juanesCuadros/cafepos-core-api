package com.cafepos.admin.negocios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Mapea usuario. Al crear un negocio se siembra el primer usuario: el Jefe,
 * con password temporal y debe_cambiar_password=true (explicito, aunque
 * coincide con el default de la columna — ver V4__usuario_debe_cambiar_password.sql).
 * empleado_id y pin_autorizacion_hash quedan NULL, no se mapean.
 */
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    private static final String ESTADO_ACTIVO = "activo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Usuario(Integer tenantId, Integer rolId, String nombre, String correo, String passwordHash) {
        this.tenantId = tenantId;
        this.rolId = rolId;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.estado = ESTADO_ACTIVO;
        this.debeCambiarPassword = true;
    }
}
