package com.cafepos.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de core-api.
 *
 * Este es el ÚNICO desplegable que contiene los módulos de negocio de
 * CaféPOS (Operación, Caja, Productos y Menú, Inventario, Compras,
 * Clientes, Personal, Gastos, Contabilidad, Reportes, Configuración,
 * Restaurante). admin-api (Super Admin) y billing-worker (facturación
 * DIAN) son proyectos Maven separados, con su propio ciclo de vida —
 * ver cafepos_arquitectura_backend_v1.md, sección 1.
 */
@SpringBootApplication
public class CoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApiApplication.class, args);
    }
}
