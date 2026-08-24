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

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Mapea facturacion_dian_resolucion (ver V1__schema_v4.sql, Modulo 10.4 de
 * api_10_restaurante.md) — de solo lectura para su PROPIO modulo (sin
 * actualizar() para prefijo/rango/etc, eso lo configura soporte tecnico
 * directo en base de datos). incrementarNumeracion() es la UNICA
 * excepcion: numeracion_actual es un contador de runtime (no
 * configuracion), que com.cafepos.core.caja necesita avanzar cada vez
 * que emite una factura real — ver FacturacionDianService.reservarSiguienteNumeroFactura.
 *
 * CRITICO DE SEGURIDAD: client_id_factus y client_secret_factus de la
 * tabla real NO se mapean aca a proposito — ni siquiera con @JsonIgnore,
 * que protege la serializacion pero no evita que un dev futuro llame
 * getClientSecretFactus() por error. Si el campo no existe en la entidad,
 * es fisicamente imposible que se filtre por este camino. Ver ademas
 * FacturacionDianResponse (DTO explicito, whitelist de campos permitidos).
 */
@Entity
@Table(name = "facturacion_dian_resolucion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacturacionDianResolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column
    private String prefijo;

    @Column(name = "rango_inicio")
    private Long rangoInicio;

    @Column(name = "rango_fin")
    private Long rangoFin;

    @Column(name = "numeracion_actual")
    private Long numeracionActual;

    @Column(name = "fecha_expedicion")
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column
    private String ambiente;

    @Column
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** Ver Javadoc de la clase — unico mutador permitido, numeracion_actual es un contador de runtime. */
    public void incrementarNumeracion() {
        this.numeracionActual = (this.numeracionActual != null ? this.numeracionActual : 0L) + 1;
        this.updatedAt = OffsetDateTime.now();
    }
}
