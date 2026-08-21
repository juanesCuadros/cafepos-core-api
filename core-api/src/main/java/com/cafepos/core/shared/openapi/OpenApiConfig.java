package com.cafepos.core.shared.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadata de Swagger UI / OpenAPI y esquema de seguridad "bearerAuth" (JWT)
 * para poder pegar el access token en el candado y probar endpoints
 * @PreAuthorize desde la UI.
 *
 * Flujo para probar desde acá: resolver tenant con X-Tenant-Slug (dev), POST
 * /auth/login con las credenciales del negocio de prueba, pegar el
 * accessToken en el candado (sin el prefijo "Bearer ") — ver
 * com.cafepos.core.shared.seguridad.SecurityConfig y AuthController.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI cafeposOpenApi(BuildProperties buildProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("CaféPOS — core-api")
                        .description("API principal de CaféPOS (Operación, Caja, Productos y Menú, "
                                + "Inventario, Compras, Clientes, Personal, Gastos, Contabilidad, "
                                + "Reportes, Configuración, Restaurante). Multi-tenant vía subdominio; "
                                + "en perfil dev, ver el header X-Tenant-Slug documentado en cada endpoint.")
                        .version(buildProperties.getVersion()))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token JWT emitido por el login. Pegar solo el "
                                        + "token (sin el prefijo \"Bearer \"), Swagger lo agrega solo.")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    /**
     * Agrega "X-Tenant-Slug" como header documentado (opcional) en todas las
     * operaciones, para que quien prueba la API en Swagger UI sepa que
     * existe. Puramente documentación — la resolución real está en
     * com.cafepos.core.shared.tenant.TenantFilter.
     */
    @Bean
    public OpenApiCustomizer tenantSlugHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> operation.addParametersItem(
                        new Parameter()
                                .in("header")
                                .name("X-Tenant-Slug")
                                .required(false)
                                .description("Solo tiene efecto en perfil dev: slug del tenant de "
                                        + "prueba (ej. \"demo\"), alternativa al subdominio "
                                        + "slug.cafepos.com para probar desde localhost. Ignorado en "
                                        + "cualquier otro perfil.")
                                .schema(new StringSchema()))));
    }
}
