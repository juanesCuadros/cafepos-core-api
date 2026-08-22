package com.cafepos.core.productosmenu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/** Mapea promocion (ver V1__schema_v4.sql, Modulo 4.5 de api_04_productos_menu.md). */
@Entity
@Table(name = "promocion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promocion {

    public static final String TIPO_PORCENTAJE = "porcentaje";
    public static final String TIPO_VALOR_FIJO = "valor_fijo";

    public static final String APLICA_PRODUCTO = "producto";
    public static final String APLICA_VENTA_TOTAL = "venta_total";

    public static final String ESTADO_ACTIVA = "activa";
    public static final String ESTADO_INACTIVA = "inactiva";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "tipo_descuento", nullable = false)
    private String tipoDescuento;

    @Column(name = "valor_descuento", nullable = false)
    private BigDecimal valorDescuento;

    @Column(name = "aplica_a", nullable = false)
    private String aplicaA;

    @Column(name = "vigencia_inicio", nullable = false)
    private LocalDate vigenciaInicio;

    @Column(name = "vigencia_fin", nullable = false)
    private LocalDate vigenciaFin;

    /** CSV interno (ej "lunes,martes") - el arreglo del JSON se convierte aca, ver DiasSemanaConverter. */
    @Column(name = "dias_semana")
    private String diasSemana;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "cantidad_minima")
    private Integer cantidadMinima;

    @Column(name = "monto_minimo")
    private BigDecimal montoMinimo;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Promocion(Integer tenantId, String nombre, String tipoDescuento, BigDecimal valorDescuento,
                      String aplicaA, LocalDate vigenciaInicio, LocalDate vigenciaFin, String diasSemana,
                      LocalTime horaInicio, LocalTime horaFin, Integer cantidadMinima, BigDecimal montoMinimo,
                      String estado) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.aplicaA = aplicaA;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFin = vigenciaFin;
        this.diasSemana = diasSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cantidadMinima = cantidadMinima;
        this.montoMinimo = montoMinimo;
        this.estado = estado != null ? estado : ESTADO_ACTIVA;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo horaInicio/horaFin/cantidadMinima/montoMinimo (JsonNullable):
     * ausente = no tocar, presente (aunque sea con valor null) = aplicar,
     * incluso para borrar el campo.
     */
    public void actualizar(String nombre, String tipoDescuento, BigDecimal valorDescuento, String aplicaA,
                            LocalDate vigenciaInicio, LocalDate vigenciaFin, JsonNullable<LocalTime> horaInicio,
                            JsonNullable<LocalTime> horaFin, JsonNullable<Integer> cantidadMinima,
                            JsonNullable<BigDecimal> montoMinimo, String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (tipoDescuento != null) {
            this.tipoDescuento = tipoDescuento;
        }
        if (valorDescuento != null) {
            this.valorDescuento = valorDescuento;
        }
        if (aplicaA != null) {
            this.aplicaA = aplicaA;
        }
        if (vigenciaInicio != null) {
            this.vigenciaInicio = vigenciaInicio;
        }
        if (vigenciaFin != null) {
            this.vigenciaFin = vigenciaFin;
        }
        if (horaInicio.isPresent()) {
            this.horaInicio = horaInicio.get();
        }
        if (horaFin.isPresent()) {
            this.horaFin = horaFin.get();
        }
        if (cantidadMinima.isPresent()) {
            this.cantidadMinima = cantidadMinima.get();
        }
        if (montoMinimo.isPresent()) {
            this.montoMinimo = montoMinimo.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * dias_semana tiene semantica propia distinta del resto de los campos:
     * tanto null explicito como arreglo vacio (a diferencia de "campo
     * ausente") SI son una accion valida - significan "sin restriccion de
     * dia" y limpian a NULL (ver DiasSemanaConverter.aCsv). Por eso vive
     * separado de actualizar(): el service solo llama esto cuando el
     * JsonNullable vino presente en el body (ver PromocionService.actualizar).
     */
    public void actualizarDiasSemana(String diasSemanaCsv) {
        this.diasSemana = diasSemanaCsv;
        this.updatedAt = OffsetDateTime.now();
    }
}
