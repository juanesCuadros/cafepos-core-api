package com.cafepos.core.gastos.domain;

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
 * Mapea categoria_gasto (ver V1__schema_v4.sql, Modulo 9). Catalogo chico,
 * solo GET/POST — el contrato no pide PATCH/DELETE (mismo criterio que
 * categoria_insumo), pero a diferencia de esa SI tiene estado
 * (activa/inactiva, ver GastoService.crear/actualizar: una categoria
 * inactiva no se puede asignar a un gasto nuevo).
 */
@Entity
@Table(name = "categoria_gasto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoriaGasto {

    public static final String ESTADO_ACTIVA = "activa";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public CategoriaGasto(Integer tenantId, String nombre) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.estado = ESTADO_ACTIVA;
        this.createdAt = OffsetDateTime.now();
    }

    public boolean estaActiva() {
        return ESTADO_ACTIVA.equals(estado);
    }
}
