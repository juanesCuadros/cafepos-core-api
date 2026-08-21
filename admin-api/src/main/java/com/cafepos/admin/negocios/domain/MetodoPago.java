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

/** Mapea metodo_pago. Al crear el negocio se siembra un unico metodo: Efectivo. */
@Entity
@Table(name = "metodo_pago")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "es_efectivo", nullable = false)
    private boolean esEfectivo;

    @Column(nullable = false)
    private String estado;

    public MetodoPago(Integer tenantId, String nombre, boolean esEfectivo, String estado) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.esEfectivo = esEfectivo;
        this.estado = estado;
    }
}
