package com.cafepos.core.shared.jackson;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra JsonNullableModule como bean de tipo com.fasterxml.jackson.databind.Module
 * — Spring Boot detecta e instala automaticamente todo bean Module en el
 * ObjectMapper autoconfigurado (Jackson2ObjectMapperBuilder), sin necesidad
 * de un ObjectMapperCustomizer explicito ni configuracion repetida por DTO.
 * Habilita JsonNullable&lt;T&gt; en los DTOs de PATCH (ver regla en CLAUDE.md).
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
