package com.cafepos.core.inventario.domain;

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
 * Mapea conteo (ver V1__schema_v4.sql, Modulo 5.4 de api_05_inventario.md).
 * Sin restriccion de frecuencia a proposito (nota resuelta del contrato) -
 * se pueden registrar varios el mismo dia.
 */
@Entity
@Table(name = "conteo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    public Conteo(Integer tenantId, Integer usuarioId) {
        this.tenantId = tenantId;
        this.usuarioId = usuarioId;
        this.fecha = OffsetDateTime.now();
    }
}
