package com.cafepos.core.shared.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Config mínima de Spring Security. Alcance actual, a propósito acotado:
 *   1. Dejar pasar Swagger UI / OpenAPI SIN autenticación, ÚNICAMENTE en
 *      perfil "dev" (bean condicionado con @Profile — si el perfil activo
 *      no es "dev", este bean no se registra y esas rutas caen en la chain
 *      de abajo, denegadas como cualquier otro endpoint: mismo 401 que
 *      todo lo demás, sin trato especial).
 *   2. Denegar por defecto TODO lo demás (anyRequest().authenticated()).
 *
 * PENDIENTE — fuera de alcance de esta tarea: no hay login, no hay filtro
 * JWT, no hay UserDetailsService/AuthenticationProvider propio. Eso
 * significa que, tal cual queda esta clase, NINGÚN endpoint fuera de
 * Swagger es alcanzable todavía (siempre 401) — es la postura segura por
 * defecto mientras la autenticación real no esté implementada. Spring Boot
 * seguirá logueando un usuario/password generado ("Using generated
 * security password...") porque no hay UserDetailsService propio; es
 * inofensivo, no hay ningún mecanismo (httpBasic/formLogin) habilitado
 * para usarlo.
 */
@Configuration
public class SecurityConfig {

    /**
     * AntPathRequestMatcher a propósito, no securityMatcher(String...) /
     * requestMatchers(String...): esas variantes resuelven a MvcRequestMatcher
     * cuando Spring MVC está en el classpath, lo que fuerza la creación
     * temprana de mvcHandlerMappingIntrospector -> resourceHandlerMapping.
     * No es la causa raíz de nada (ese bean falla igual apenas springdoc-ui
     * está habilitado, ver pom.xml junto a springdoc.version), pero evita
     * esa cadena de beans extra sin ningún costo.
     */
    private static final RequestMatcher SWAGGER_MATCHER = new OrRequestMatcher(
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/v3/api-docs.yaml")
    );

    @Bean
    @Profile("dev")
    @Order(1)
    public SecurityFilterChain swaggerDevFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(SWAGGER_MATCHER)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
