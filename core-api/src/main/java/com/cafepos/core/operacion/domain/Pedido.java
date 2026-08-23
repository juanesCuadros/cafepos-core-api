package com.cafepos.core.operacion.domain;

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

/** Mapea pedido (ver V1__schema_v4.sql). */
@Entity
@Table(name = "pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pedido {

    public static final String TIPO_MESA = "mesa";
    public static final String TIPO_VENTA_RAPIDA = "venta_rapida";

    public static final String ESTADO_ABIERTO = "abierto";
    public static final String ESTADO_ENVIADO = "enviado";
    public static final String ESTADO_LISTO = "listo";
    public static final String ESTADO_CERRADO = "cerrado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "mesa_id")
    private Integer mesaId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "numero_orden", nullable = false)
    private String numeroOrden;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_apertura", nullable = false)
    private OffsetDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;

    public Pedido(Integer tenantId, Integer mesaId, Integer usuarioId, String tipo) {
        this.tenantId = tenantId;
        this.mesaId = mesaId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.numeroOrden = "";
        this.estado = ESTADO_ABIERTO;
        this.fechaApertura = OffsetDateTime.now();
    }

    /** El numero_orden se arma DESPUES del INSERT, con el id ya asignado (ver PedidoService.abrir). */
    public void asignarNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public boolean estaAbierto() {
        return !ESTADO_CERRADO.equals(estado);
    }

    /** Idempotente a proposito: reenviar una comanda ya enviada no es un error. */
    public void enviarComanda() {
        this.estado = ESTADO_ENVIADO;
    }

    public void marcarTodosListos() {
        this.estado = ESTADO_LISTO;
    }

    public void moverA(Integer nuevaMesaId) {
        this.mesaId = nuevaMesaId;
    }
}
