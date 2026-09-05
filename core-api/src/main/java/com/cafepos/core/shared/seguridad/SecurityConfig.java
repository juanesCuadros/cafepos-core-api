package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.excepciones.FilterErrorWriter;
import com.cafepos.core.shared.tenant.SuscripcionFilter;
import com.cafepos.core.shared.tenant.TenantFilter;
import com.cafepos.core.shared.tenant.TenantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
 * Orden de filtros dentro de la chain autenticada, y por que:
 *
 *   1. JwtAuthenticationFilter — puebla el SecurityContext si el request
 *      trae un JWT valido. Corre PRIMERO a proposito: todo lo que sigue
 *      necesita saber si hay JWT valido o no antes de decidir nada.
 *   2. TenantFilter (com.cafepos.core.shared.tenant) — resuelve el tenant.
 *      Si JwtAuthenticationFilter ya autentico el request, el tenant_id
 *      sale EXCLUSIVAMENTE del claim del JWT (X-Tenant-Slug ni se lee). Si
 *      no hay JWT, resuelve por X-Tenant-Slug o subdominio. Por eso tiene
 *      que correr DESPUES de JwtAuthenticationFilter — no puede saber cual
 *      de las dos fuentes usar sin esa informacion.
 *   3. SuscripcionFilter (com.cafepos.core.shared.tenant) — necesita
 *      TenantContext ya resuelto por TenantFilter, por eso va justo
 *      despues.
 *   4. DebeCambiarPasswordFilter — lee el resultado de JwtAuthenticationFilter
 *      (por eso addFilterAfter, nunca antes) para bloquear todo menos
 *      /auth/cambiar-password-inicial mientras el flag este en true. No
 *      depende de tenant, pero va al final por ser el ultimo gate antes
 *      del controller.
 *
 * Ni TenantFilter ni SuscripcionFilter son @Component: si lo fueran, Spring
 * Boot los registraria ADEMAS como filtros de servlet globales (aplicando a
 * TODOS los requests, sin importar esta chain), duplicando su ejecucion y
 * rompiendo el orden relativo a JwtAuthenticationFilter que este comentario
 * describe. Se instancian a mano aca, igual que JwtAuthenticationFilter y
 * DebeCambiarPasswordFilter.
 *
 * @EnableMethodSecurity + methodSecurityExpressionHandler() de mas abajo:
 * habilita @PreAuthorize con hasPermission('modulo.subvista', 'accion') en
 * cualquier controller de cualquier modulo, resuelto por PermisoEvaluator
 * (RBAC dinamico, ver su Javadoc) en vez del PermissionEvaluator por
 * defecto de Spring (que siempre deniega si no se lo reemplaza).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                                              FilterErrorWriter filterErrorWriter,
                                              TenantRepository tenantRepository,
                                              @Value("${cafepos.tenant.base-domain}") String tenantBaseDomain,
                                              JwtBlacklistService jwtBlacklistService,
                                              CorsConfigurationSource corsConfigurationSource) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, jwtBlacklistService, filterErrorWriter);
        TenantFilter tenantFilter = new TenantFilter(tenantBaseDomain, tenantRepository, filterErrorWriter);
        SuscripcionFilter suscripcionFilter = new SuscripcionFilter(tenantRepository, filterErrorWriter);
        DebeCambiarPasswordFilter debeCambiarPasswordFilter = new DebeCambiarPasswordFilter(filterErrorWriter);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // /ws/** permitAll aca a proposito: SIGUE exigiendo un
                        // access token valido igual, pero lo valida
                        // JwtHandshakeInterceptor (shared.websocket) leyendo
                        // ?token=... del query param — el navegador no puede
                        // mandar el header Authorization en el handshake inicial
                        // de un WebSocket. Sin este permitAll, esta cadena HTTP
                        // normal (que solo sabe leer el header) rechaza el
                        // handshake con 401 antes de que el interceptor de WS
                        // llegue a correr.
                        .requestMatchers(RutasAuth.LOGIN, RutasAuth.REFRESH, RutasAuth.LOGOUT, "/menu-publico", "/ws/**", "/uploads/**")
                        .permitAll()
                        .anyRequest().authenticated())
                // Sin esto, Spring Security responde 403 tanto para "no estas
                // autenticado" (token ausente/invalido/vencido) como para "no
                // tenes permiso" — ambiguo para el cliente, y ademas rompe el
                // refresh automatico del frontend (httpClient.ts solo reintenta
                // con un token nuevo cuando ve 401, nunca ante un 403). Con esto,
                // 401 = no autenticado, 403 = autenticado pero sin permiso.
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                        filterErrorWriter.escribir(response, HttpStatus.UNAUTHORIZED.value(),
                                "Sesión inválida o expirada, inicia sesión nuevamente", "NO_AUTENTICADO")))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantFilter, JwtAuthenticationFilter.class)
                .addFilterAfter(suscripcionFilter, TenantFilter.class)
                .addFilterAfter(debeCambiarPasswordFilter, SuscripcionFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cafepos.cors.allowed-origin-patterns}") List<String> allowedOriginPatterns) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOriginPatterns);
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

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(PermissionEvaluator permisoEvaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permisoEvaluator);
        return handler;
    }
}