package com.cafepos.core.shared.tenant;

import jakarta.persistence.EntityManager;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Ejecuta "SELECT set_config(...)" (equivalente a SET LOCAL, pero soporta
 * bind parameter — SET LOCAL no) como PRIMERA sentencia de cada transaccion,
 * inmediatamente despues de que la
 * transaccion fisica arranca (super.doBegin ya adquirio la conexion y puso
 * autocommit=false) y antes de que cualquier repositorio ejecute una query.
 * Es la pieza que activa Row-Level Security para de verdad — sin esto,
 * "current_setting('app.current_tenant_id')" en las policies de V1 no
 * tiene ningun valor seteado y Postgres rechaza la query con un error.
 *
 * Si TenantContext no tiene tenant_id resuelto (ej. la propia query que
 * TenantFilter usa para resolver el slug -> tenant_id, o un endpoint sin
 * tenant como el health check), no se ejecuta el SET LOCAL — no hace falta,
 * ninguna tabla con RLS activo se toca en esos casos.
 *
 * Reemplaza el JpaTransactionManager que Spring Boot autoconfigura por
 * defecto (ver TenantTransactionConfig) — @ConditionalOnMissingBean hace
 * que esta definicion gane.
 */
public class TenantAwareJpaTransactionManager extends JpaTransactionManager {

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        Integer tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            return;
        }
        EntityManagerHolder holder = (EntityManagerHolder)
                TransactionSynchronizationManager.getResource(getEntityManagerFactory());
        if (holder == null) {
            return;
        }
        EntityManager entityManager = holder.getEntityManager();
        entityManager.createNativeQuery("SELECT set_config('app.current_tenant_id', :tenantId, true)")
                .setParameter("tenantId", tenantId.toString())
                .getSingleResult();
    }
}
