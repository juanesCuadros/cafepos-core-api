package com.cafepos.core.restaurante.domain;

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
 * Mapea menu_digital_config (ver V1__schema_v4.sql, Modulo 10.5 de
 * api_10_restaurante.md) — a diferencia de restaurantes/metodo_pago, esta
 * fila NO se provisiona al dar de alta el tenant: se crea recien la
 * primera vez que se activa el menu (ver MenuDigitalService).
 */
@Entity
@Table(name = "menu_digital_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuDigitalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "url_publica")
    private String urlPublica;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public MenuDigitalConfig(Integer tenantId) {
        this.tenantId = tenantId;
        this.activo = false;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * urlPublica se persiste UNA sola vez, la primera vez que se activa (si
     * todavia esta vacia) — se eligio persistir en vez de recalcular en
     * cada GET para que el link/QR que el negocio ya imprimio siga siendo
     * valido aunque el criterio de armado de la URL cambie mas adelante.
     */
    public void activar(String urlPublicaSiVacia) {
        this.activo = true;
        if (this.urlPublica == null) {
            this.urlPublica = urlPublicaSiVacia;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
        this.updatedAt = OffsetDateTime.now();
    }
}
