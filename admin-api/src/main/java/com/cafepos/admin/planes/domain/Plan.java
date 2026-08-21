package com.cafepos.admin.planes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Mapea planes (capa plataforma, creada por core-api en V1__schema_v4.sql, dias_prueba en V6). */
@Entity
@Table(name = "planes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(name = "precio_mensual", nullable = false)
    private BigDecimal precioMensual;

    @Column(name = "limite_usuarios")
    private Integer limiteUsuarios;

    @Column(name = "dias_prueba", nullable = false)
    private int diasPrueba;

    @Column(nullable = false)
    private String estado;

    public Plan(String nombre, String descripcion, BigDecimal precioMensual, Integer limiteUsuarios, int diasPrueba) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioMensual = precioMensual;
        this.limiteUsuarios = limiteUsuarios;
        this.diasPrueba = diasPrueba;
        this.estado = ESTADO_ACTIVO;
    }

    public void actualizar(String nombre, String descripcion, BigDecimal precioMensual, Integer limiteUsuarios, int diasPrueba) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioMensual = precioMensual;
        this.limiteUsuarios = limiteUsuarios;
        this.diasPrueba = diasPrueba;
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
