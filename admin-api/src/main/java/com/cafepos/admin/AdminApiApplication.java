package com.cafepos.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de admin-api: backend del Panel Super Admin.
 *
 * Proyecto Maven separado de core-api, sin sesion compartida (JWT propio,
 * ver auth.infrastructure.security.JwtService) ni Spring Modulith (servicio
 * chico, no se justifica esa complejidad todavia — ver CLAUDE.md).
 *
 * @EnableScheduling: requerido para que @Scheduled funcione (ver
 * negocios.infrastructure.VencimientoPruebaJob).
 */
@SpringBootApplication
@EnableScheduling
public class AdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApiApplication.class, args);
    }
}
