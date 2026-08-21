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

/** Mapea restaurantes. Solo nombre_negocio — el resto queda NULL al crear el negocio. */
@Entity
@Table(name = "restaurantes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private Integer tenantId;

    @Column(name = "nombre_negocio", nullable = false)
    private String nombreNegocio;

    public Restaurante(Integer tenantId, String nombreNegocio) {
        this.tenantId = tenantId;
        this.nombreNegocio = nombreNegocio;
    }
}
