package com.cafepos.core.shared.openapi;

/**
 * Nombres canónicos de los grupos ("@Tag") de Swagger UI, uno por módulo de
 * negocio. Usar en cada controller así: {@code @Tag(name = ApiTags.CAJA)}.
 * Mantiene la agrupación consistente aunque distintos controllers de un
 * mismo módulo se creen en momentos distintos.
 */
public final class ApiTags {

    public static final String OPERACION = "Operación";
    public static final String CAJA = "Caja";
    public static final String PRODUCTOS_MENU = "Productos y Menú";
    public static final String INVENTARIO = "Inventario";
    public static final String COMPRAS = "Compras";
    public static final String CLIENTES = "Clientes";
    public static final String PERSONAL = "Personal";
    public static final String GASTOS = "Gastos";
    public static final String CONTABILIDAD = "Contabilidad";
    public static final String REPORTES = "Reportes";
    public static final String CONFIGURACION = "Configuración";
    public static final String RESTAURANTE = "Restaurante";
    public static final String AUTENTICACION = "Autenticación";

    private ApiTags() {
    }
}
