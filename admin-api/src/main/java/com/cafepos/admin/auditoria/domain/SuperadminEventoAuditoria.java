package com.cafepos.admin.auditoria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

/** Mapea superadmin_evento_auditoria (creada en V30). */
@Entity
@Table(name = "superadmin_evento_auditoria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuperadminEventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "superadmin_id")
    private Integer superadminId;

    @Column(nullable = false)
    private String accion;

    @Column(name = "entidad_tipo", nullable = false)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Integer entidadId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_antes", columnDefinition = "jsonb")
    private String datosAntes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_despues", columnDefinition = "jsonb")
    private String datosDespues;

    @Column(name = "ip_origen")
    private String ipOrigen;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "fecha_hora", insertable = false, updatable = false)
    private OffsetDateTime fechaHora;

    public SuperadminEventoAuditoria(Integer superadminId, String accion, String entidadTipo, Integer entidadId,
                                     String datosAntes, String datosDespues, String ipOrigen, String userAgent) {
        this.superadminId = superadminId;
        this.accion = accion;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.datosAntes = datosAntes;
        this.datosDespues = datosDespues;
        this.ipOrigen = ipOrigen;
        this.userAgent = userAgent;
    }
}
