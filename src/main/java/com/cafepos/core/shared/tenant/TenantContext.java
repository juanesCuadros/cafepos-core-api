package com.cafepos.core.shared.tenant;

/**
 * Slug del tenant resuelto para el request actual (ver TenantFilter).
 *
 * Guarda únicamente el SLUG, no el tenant_id numérico: convertir slug -> id
 * requiere consultar la tabla `tenants`, y todavía no existe ninguna
 * entidad/repositorio JPA en el proyecto (ver TenantFilter para el detalle
 * de qué falta para que Row-Level Security quede realmente aplicado).
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_SLUG = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentSlug(String slug) {
        CURRENT_SLUG.set(slug);
    }

    public static String getCurrentSlug() {
        return CURRENT_SLUG.get();
    }

    public static void clear() {
        CURRENT_SLUG.remove();
    }
}
