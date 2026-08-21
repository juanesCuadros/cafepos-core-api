package com.cafepos.admin.shared;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadata de Swagger UI / OpenAPI y esquema de seguridad "bearerAuth" (JWT)
 * para poder pegar el access token en el candado y probar bootstrap/login/
 * refresh desde la UI. SecurityConfig ya deja pasar estas rutas sin
 * autenticacion solo en @Profile("dev") — ver su Javadoc.
 *
 * Sin header de tenant aca a proposito: admin-api no tiene el problema de
 * multi-tenancy de core-api, Super Admin no pertenece a ningun tenant.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI adminApiOpenApi(BuildProperties buildProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("CaféPOS — admin-api")
                        .description("Backend exclusivo del Panel Super Admin de CaféPOS — no es la "
                                + "API de las cafeterías (eso es core-api). Este servicio opera entre "
                                + "tenants: alta de negocios, suscripciones, gestión de Super Admins.")
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
}
