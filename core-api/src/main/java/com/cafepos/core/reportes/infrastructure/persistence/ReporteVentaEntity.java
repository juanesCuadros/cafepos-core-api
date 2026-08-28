package com.cafepos.core.reportes.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad dummy para habilitar el uso de TenantAwareRepository
 * en las consultas nativas de reportes.
 */
@Entity(name = "ReporteVenta")
@Table(name = "venta")
class ReporteVentaEntity {

    @Id
    private Integer id;

    protected ReporteVentaEntity() {
    }
}
