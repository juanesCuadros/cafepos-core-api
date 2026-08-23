package com.cafepos.core.shared.seguridad;

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
 * Mapea evento_seguridad. Separada de evento_auditoria a proposito (ver
 * V1__schema_v4.sql) — usada para registrar 'refresh_token_reuso' (un
 * refresh token ya revocado que se vuelve a presentar es señal de robo,
 * ver RefreshTokenService), 'login_fallido' (cada intento de login con
 * password incorrecta, ver LoginService y Usuario.registrarIntentoFallido
 * para el bloqueo de RN-008 tras 5 intentos) y 'pin_fallido' (cada intento
 * de PIN de step-up incorrecto, ver PinVerificarService y
 * Usuario.registrarPinIntentoFallido). Los cuatro valores de tipo_evento ya
 * estan permitidos por el CHECK de la tabla desde V1 (schema v4).
 */
@Entity
@Table(name = "evento_seguridad")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventoSeguridad {

    public static final String TIPO_REFRESH_TOKEN_REUSO = "refresh_token_reuso";
    public static final String TIPO_LOGIN_FALLIDO = "login_fallido";
    public static final String TIPO_PIN_FALLIDO = "pin_fallido";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @Column
    private String detalle;

    public EventoSeguridad(Integer tenantId, Integer usuarioId, String tipoEvento, String detalle) {
        this.tenantId = tenantId;
        this.usuarioId = usuarioId;
        this.tipoEvento = tipoEvento;
        this.detalle = detalle;
    }
}
