package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


/**
 * Config de Spring Security:
 *   1. Swagger UI / OpenAPI SIN autenticación, ÚNICAMENTE en perfil "dev".
 *   2. /auth/login, /auth/refresh y /auth/logout publicos (permitAll) — ninguno
 *      de los tres depende de un access token valido (login todavia no tiene
 *      uno; refresh y logout operan solo sobre el refresh token opaco, que
 *      puede seguir vigente aunque el access token ya haya expirado).
 *      /auth/cambiar-password-inicial NO esta en esta lista: exige JWT
 *      valido, igual que cualquier otro endpoint (ver DebeCambiarPasswordFilter
 *      para la logica de que sea el UNICO alcanzable mientras el flag siga
 *      en true).
 *   3. Todo lo demas exige JWT valido (anyRequest().authenticated()).
 *
 * Orden de filtros dentro de la chain autenticada — JwtAuthenticationFilter
 * puebla el SecurityContext, DebeCambiarPasswordFilter lee ese resultado
 * (por eso addFilterAfter, nunca antes) para bloquear todo menos
 * /auth/cambiar-password-inicial mientras el flag este en true.
 *
 * TenantFilter (com.cafepos.core.shared.tenant) corre ANTES de toda esta
 * chain (@Order en HIGHEST_PRECEDENCE, filtro de servlet plano, no
 * registrado aca) — para cuando se llega a JwtAuthenticationFilter, el
 * tenant del request ya esta resuelto.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
    public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtService jwtService,
                                              FilterErrorWriter filterErrorWriter) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        DebeCambiarPasswordFilter debeCambiarPasswordFilter = new DebeCambiarPasswordFilter(filterErrorWriter);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(RutasAuth.LOGIN, RutasAuth.REFRESH, RutasAuth.LOGOUT).permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(debeCambiarPasswordFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("https://*.resttodash.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant-Slug"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}