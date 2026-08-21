package com.cafepos.core.shared.tenant;

/**
 * Tenant resuelto para el request actual (ver TenantFilter). Guarda el slug
 * Y el tenant_id numerico: el id lo resuelve TenantFilter contra la tabla
 * tenants, apenas arranca el request, antes de que corra cualquier otra
 * cosa. TenantAwareJpaTransactionManager lee getCurrentTenantId() para
 * ejecutar "SET LOCAL app.current_tenant_id" al abrir cada transaccion
 * (ver shared.tenant, requisito duro de Row-Level Security).
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_SLUG = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURRENT_TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentSlug(String slug) {
        CURRENT_SLUG.set(slug);
    }

    public static String getCurrentSlug() {
        return CURRENT_SLUG.get();
    }

    public static void setCurrentTenantId(Integer tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    public static Integer getCurrentTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    public static void clear() {
        CURRENT_SLUG.remove();
        CURRENT_TENANT_ID.remove();
    }
}
