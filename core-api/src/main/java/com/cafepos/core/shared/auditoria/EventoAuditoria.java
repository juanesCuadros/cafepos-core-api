package com.cafepos.core.shared.auditoria;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Mapea evento_auditoria (ver V1__schema_v4.sql). Append-only por diseño de grants (app_tenant sin UPDATE/DELETE). */
@Entity
@Table(name = "evento_auditoria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "usuario_autoriza_id")
    private Integer usuarioAutorizaId;

    @Column(name = "permiso_id")
    private Integer permisoId;

    @Column(name = "entidad_tipo", nullable = false)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Integer entidadId;

    @Column(nullable = false)
    private String accion;

    /**
     * @Immutable a proposito: sin esto, Hibernate marca estos campos JSON
     * como "dirty" en el flush de commit aunque nunca cambien despues del
     * INSERT (snapshot de dirty-checking no estable para JsonNode via
     * @JdbcTypeCode(SqlTypes.JSON)) — confirmado real: sin @Immutable,
     * cada guardado emite un INSERT exitoso seguido de un UPDATE
     * redundante con TODAS las columnas, que ademas rompe append-only
     * (app_tenant no tiene UPDATE sobre evento_auditoria a proposito).
     */
    @Column(name = "datos_antes")
    @JdbcTypeCode(SqlTypes.JSON)
    @Immutable
    private JsonNode datosAntes;

    @Column(name = "datos_despues")
    @JdbcTypeCode(SqlTypes.JSON)
    @Immutable
    private JsonNode datosDespues;

    @Column(name = "ip_origen")
    private String ipOrigen;

    @Column(name = "user_agent")
    private String userAgent;

    public EventoAuditoria(Integer tenantId, Integer usuarioId, Integer usuarioAutorizaId, Integer permisoId,
                            String entidadTipo, Integer entidadId, String accion, JsonNode datosAntes,
                            JsonNode datosDespues, String ipOrigen, String userAgent) {
        this.tenantId = tenantId;
        this.usuarioId = usuarioId;
        this.usuarioAutorizaId = usuarioAutorizaId;
        this.permisoId = permisoId;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.accion = accion;
        this.datosAntes = datosAntes;
        this.datosDespues = datosDespues;
        this.ipOrigen = ipOrigen;
        this.userAgent = userAgent;
    }
}
