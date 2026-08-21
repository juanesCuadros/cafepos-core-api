package com.cafepos.core.shared.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Mapea tenants_resolucion (vista de solo lectura sobre "tenants", ver
 * V9__tenants_vista_resolucion.sql) — NO la tabla "tenants" completa.
 * "tenants" es la unica tabla del sistema sin RLS (no tiene tenant_id), asi
 * que un GRANT directo ahi expondria plan_id, superadmin_aprobador_id, etc.
 * de TODOS los negocios a cualquier query bajo app_tenant. La vista solo
 * expone id, slug y estado — lo unico que el flujo de autenticacion
 * necesita para resolver el tenant y chequear si esta suspendido.
 *
 * @Immutable: app_tenant no tiene (ni deberia tener) INSERT/UPDATE/DELETE
 * sobre la vista ni sobre la tabla base — mutar el estado de un tenant es
 * responsabilidad exclusiva de admin-api. Le evita a Hibernate el dirty
 * checking y cualquier intento de UPDATE contra una vista de solo lectura.
 */
@Entity
@Table(name = "tenants_resolucion")
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_PRUEBA = "prueba";
    public static final String ESTADO_SUSPENDIDO = "suspendido";
    public static final String ESTADO_CANCELADO = "cancelado";

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String estado;

    public boolean estaSuspendidoOCancelado() {
        return ESTADO_SUSPENDIDO.equals(estado) || ESTADO_CANCELADO.equals(estado);
    }

    public String mensajeBloqueo() {
        if (ESTADO_CANCELADO.equals(estado)) {
            return "Este negocio fue cancelado, contacta a soporte si crees que es un error";
        }
        return "Este negocio está suspendido, contacta a soporte para reactivarlo";
    }
}
