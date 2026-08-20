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
 * Mapea superadmin_refresh_token (ver V1__schema_v4.sql). tokenHash guarda
 * un SHA-256 del token opaco, no un hash bcrypt: el token ya es un valor
 * aleatorio de alta entropia (no una contrasena elegida por un humano), asi
 * que no hace falta un hash lento por row — se busca directo por
 * WHERE token_hash = ?, algo que bcrypt no permite sin recorrer cada fila.
 */
@Entity
@Table(name = "superadmin_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuperadminRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "superadmin_id", nullable = false)
    private Integer superadminId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;

    @Column(name = "ultimo_uso_en", nullable = false)
    private OffsetDateTime ultimoUsoEn;

    @Column(nullable = false)
    private boolean revocado;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public SuperadminRefreshToken(Integer superadminId, String tokenHash, OffsetDateTime expiraEn) {
        this.superadminId = superadminId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
        this.ultimoUsoEn = OffsetDateTime.now();
        this.revocado = false;
    }

    public boolean estaVencido() {
        return OffsetDateTime.now().isAfter(expiraEn);
    }

    public void revocar() {
        this.revocado = true;
        this.ultimoUsoEn = OffsetDateTime.now();
    }
}
