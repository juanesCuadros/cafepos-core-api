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

/** Mapea configuracion_sistema. Solo tenant_id — el resto de columnas usa el default de la base. */
@Entity
@Table(name = "configuracion_sistema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private Integer tenantId;

    public ConfiguracionSistema(Integer tenantId) {
        this.tenantId = tenantId;
    }
}
