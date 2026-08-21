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

import java.time.OffsetDateTime;

/**
 * Mapea usuario_refresh_token. Rotado en cada uso: revocado apenas se usa,
 * sin importar si el par nuevo se llega a usar (ver RefreshTokenService).
 * tokenHash es SHA-256 del token opaco, no bcrypt — mismo criterio que
 * admin-api (ver RefreshTokenIssuer): el token ya nace con alta entropia,
 * hace falta un WHERE token_hash = ? directo, no una verificacion fila por fila.
 */
@Entity
@Table(name = "usuario_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsuarioRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;

    @Column(name = "ultimo_uso_en", nullable = false)
    private OffsetDateTime ultimoUsoEn;

    @Column(nullable = false)
    private boolean revocado;

    public UsuarioRefreshToken(Integer tenantId, Integer usuarioId, String tokenHash, OffsetDateTime expiraEn) {
        this.tenantId = tenantId;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
        this.ultimoUsoEn = OffsetDateTime.now();
        this.revocado = false;
    }

    public boolean estaVencido() {
        return OffsetDateTime.now().isAfter(expiraEn);
    }

    public long minutosDeInactividad() {
        return java.time.Duration.between(ultimoUsoEn, OffsetDateTime.now()).toMinutes();
    }

    public void revocar() {
        this.revocado = true;
        this.ultimoUsoEn = OffsetDateTime.now();
    }
}
