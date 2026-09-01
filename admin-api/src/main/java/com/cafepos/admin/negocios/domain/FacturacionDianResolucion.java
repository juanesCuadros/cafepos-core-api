package com.cafepos.admin.negocios.domain;

import com.cafepos.admin.shared.criptografia.FactusCredencialAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
 * Mapea facturacion_dian_resolucion (tabla creada por core-api, ver
 * V1__schema_v4.sql y V20__facturacion_dian_resolucion_credenciales_factus_completas.sql
 * en el repositorio de core-api — admin-api no tiene migraciones propias,
 * ver CLAUDE.md). Unico proposito de esta entidad en admin-api: que Super
 * Admin pueda configurar las credenciales Factus de cualquier tenant (ver
 * FacturacionDianAdminService, unico caller). prefijo/fecha_expedicion/
 * fecha_vencimiento SI se mapean desde 01-sep-2026 (ver INTEGRACION.md
 * hallazgo 3.45): antes no habia NINGUNA forma de configurarlos por API y
 * habia que escribirlos a mano en la base, aunque son datos que la DIAN
 * asigna por resolucion y cambian por tenant. numeracion_actual sigue sin
 * exponerse a proposito: es un contador de runtime que solo core-api debe
 * mover (reservarSiguienteNumeroFactura), nunca Super Admin a mano.
 *
 * CRITICO DE SEGURIDAD: client_id_factus/client_secret_factus/username_factus/
 * password_factus SI se mapean (necesario para que @Convert cifre) pero
 * @Getter(AccessLevel.NONE) bloquea a Lombok de generar getters publicos —
 * ninguna otra clase puede leerlos de vuelta desde este proyecto, admin-api
 * solo ESCRIBE credenciales, nunca las necesita releer (quien las descifra
 * es core-api, para llamar a Factus de verdad).
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

    @Column(name = "fecha_expedicion")
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "rango_inicio")
    private Long rangoInicio;

    @Column(name = "rango_fin")
    private Long rangoFin;

    @Column(name = "numeracion_actual")
    private Long numeracionActual;

    @Column
    private String ambiente;

    @Column
    private String estado;

    @Getter(AccessLevel.NONE)
    @Column(name = "client_id_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String clientIdFactus;

    @Getter(AccessLevel.NONE)
    @Column(name = "client_secret_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String clientSecretFactus;

    @Getter(AccessLevel.NONE)
    @Column(name = "username_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String usernameFactus;

    @Getter(AccessLevel.NONE)
    @Column(name = "password_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String passwordFactus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static FacturacionDianResolucion crear(Integer tenantId) {
        FacturacionDianResolucion resolucion = new FacturacionDianResolucion();
        resolucion.tenantId = tenantId;
        resolucion.numeracionActual = 0L;
        resolucion.createdAt = OffsetDateTime.now();
        resolucion.updatedAt = OffsetDateTime.now();
        return resolucion;
    }

    /**
     * Unico punto de escritura de credenciales Factus — alta o actualizacion
     * (ver FacturacionDianAdminService, find-or-create por tenant_id).
     * numeracion_actual se reinicia a 0 en ambos casos: aceptable porque
     * Super Admin reconfigura credenciales como operacion excepcional, no
     * algo que corra con facturas reales ya numeradas en progreso (mismo
     * criterio ya aceptado en core-api).
     */
    public void configurarCredencialesFactus(String clientIdFactus, String clientSecretFactus, String usernameFactus,
                                              String passwordFactus, Long rangoInicio, Long rangoFin, String ambiente,
                                              String estado, String prefijo, LocalDate fechaExpedicion,
                                              LocalDate fechaVencimiento) {
        this.clientIdFactus = clientIdFactus;
        this.clientSecretFactus = clientSecretFactus;
        this.usernameFactus = usernameFactus;
        this.passwordFactus = passwordFactus;
        this.rangoInicio = rangoInicio;
        this.rangoFin = rangoFin;
        this.ambiente = ambiente;
        this.estado = estado;
        // prefijo/fechas: null significa "no tocar", NO "borrar" — son
        // opcionales en el request para no romper integraciones que ya
        // llamaban a este endpoint sin ellos, y reconfigurar credenciales no
        // deberia perder el prefijo real que la DIAN asigno por resolucion.
        if (prefijo != null) {
            this.prefijo = prefijo;
        }
        if (fechaExpedicion != null) {
            this.fechaExpedicion = fechaExpedicion;
        }
        if (fechaVencimiento != null) {
            this.fechaVencimiento = fechaVencimiento;
        }
        this.numeracionActual = 0L;
        this.updatedAt = OffsetDateTime.now();
    }
}
