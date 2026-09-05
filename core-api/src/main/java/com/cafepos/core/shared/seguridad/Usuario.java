package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.OffsetDateTime;

/**
 * Mapea usuario (ver V1__schema_v4.sql). El primer usuario de un tenant
 * (el Jefe) lo provisiona admin-api al dar de alta el negocio — el alta de
 * usuarios ADICIONALES dentro de un tenant ya existente es responsabilidad
 * de com.cafepos.core.configuracion (Modulo 11.1), via el constructor
 * publico de mas abajo.
 */
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    /** RN-008: 5 intentos fallidos consecutivos bloquean el login por 15 minutos. */
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final long BLOQUEO_MINUTOS = 15;

    /** Contador separado del de login (V17) — 5 PIN fallidos consecutivos bloquean el PIN por 30 minutos. */
    private static final int MAX_PIN_INTENTOS_FALLIDOS = 5;
    private static final long PIN_BLOQUEO_MINUTOS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "empleado_id")
    private Integer empleadoId;

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

    /** Solo usuarios con rol Admin o Jefe pueden tenerlo — nunca se expone en una respuesta. */
    @Column(name = "pin_autorizacion_hash")
    private String pinAutorizacionHash;

    @Column(nullable = false)
    private String estado;

    @Column(name = "debe_cambiar_password", nullable = false)
    private boolean debeCambiarPassword;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;

    @Column(name = "pin_intentos_fallidos", nullable = false)
    private int pinIntentosFallidos;

    @Column(name = "pin_bloqueado_hasta")
    private OffsetDateTime pinBloqueadoHasta;

    /**
     * Alta de un usuario adicional dentro de un tenant existente (ver
     * com.cafepos.core.configuracion, POST /usuarios). Nace siempre con
     * debeCambiarPassword=true, mismo mecanismo que ya fuerza el cambio de
     * password temporal en el primer login (ver DebeCambiarPasswordFilter).
     */
    public Usuario(Integer tenantId, Integer empleadoId, Integer rolId, String nombre, String correo,
                    String passwordHash, String pinAutorizacionHash, String estado) {
        this.tenantId = tenantId;
        this.empleadoId = empleadoId;
        this.rolId = rolId;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.pinAutorizacionHash = pinAutorizacionHash;
        this.estado = estado;
        this.debeCambiarPassword = true;
        this.intentosFallidos = 0;
    }

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

    public boolean estaPinBloqueado() {
        return pinBloqueadoHasta != null && pinBloqueadoHasta.isAfter(OffsetDateTime.now());
    }

    /**
     * Al llegar a MAX_PIN_INTENTOS_FALLIDOS, bloquea y resetea el contador
     * para que el proximo ciclo cuente desde cero — mismo criterio que
     * registrarIntentoFallido() para login, contador independiente. Devuelve
     * los intentos restantes antes del bloqueo, para que el 403 de
     * PinVerificarService pueda informarlo.
     */
    public int registrarPinIntentoFallido() {
        pinIntentosFallidos++;
        if (pinIntentosFallidos >= MAX_PIN_INTENTOS_FALLIDOS) {
            pinBloqueadoHasta = OffsetDateTime.now().plusMinutes(PIN_BLOQUEO_MINUTOS);
            pinIntentosFallidos = 0;
            return 0;
        }
        return MAX_PIN_INTENTOS_FALLIDOS - pinIntentosFallidos;
    }

    /** No toca pinBloqueadoHasta, mismo motivo que registrarLoginExitoso(). */
    public void registrarPinExitoso() {
        pinIntentosFallidos = 0;
    }

    /**
     * PATCH /usuarios/{id} (ver com.cafepos.core.configuracion) — nunca
     * incluye password, eso cambia solo por el flujo propio de cambio de
     * password. rolId se queda con tipo plano porque es NOT NULL en la
     * tabla; empleadoId y pinAutorizacionHash son JsonNullable porque son
     * genuinamente nullable de negocio (ver regla de DTOs de PATCH en
     * CLAUDE.md).
     */
    public void actualizar(String nombre, String correo, Integer rolId, JsonNullable<Integer> empleadoId,
                            JsonNullable<String> pinAutorizacionHash, String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (correo != null) {
            this.correo = correo;
        }
        if (rolId != null) {
            this.rolId = rolId;
        }
        if (empleadoId.isPresent()) {
            this.empleadoId = empleadoId.get();
        }
        if (pinAutorizacionHash.isPresent()) {
            this.pinAutorizacionHash = pinAutorizacionHash.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
    }

    public void inactivar() {
        this.estado = ESTADO_INACTIVO;
    }
}
