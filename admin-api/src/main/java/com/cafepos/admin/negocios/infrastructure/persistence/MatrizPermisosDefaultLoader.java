package com.cafepos.admin.negocios.infrastructure.persistence;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Carga la matriz de permisos por defecto (matriz-permisos-default.csv),
 * transcrita de los comentarios de V2__catalogo_permisos.sql en core-api —
 * esa fuente NO se inserta como SQL porque rol_permiso es tenant-scoped,
 * asi que se aplica programaticamente cada vez que se crea un negocio.
 */
@Component
public class MatrizPermisosDefaultLoader {

    public record Fila(String modulo, String accion, boolean jefe, boolean admin,
                        boolean cajero, boolean mesero, boolean cocina) {

        public String clave() {
            return modulo + "|" + accion;
        }
    }

    private final List<Fila> filas;

    public MatrizPermisosDefaultLoader() {
        this.filas = cargar();
    }

    public List<Fila> filas() {
        return filas;
    }

    private List<Fila> cargar() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("matriz-permisos-default.csv").getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().skip(1).filter(linea -> !linea.isBlank()).map(this::parsear).toList();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar matriz-permisos-default.csv", e);
        }
    }

    private Fila parsear(String linea) {
        String[] p = linea.split(",");
        return new Fila(p[0], p[1], Boolean.parseBoolean(p[2]), Boolean.parseBoolean(p[3]),
                Boolean.parseBoolean(p[4]), Boolean.parseBoolean(p[5]), Boolean.parseBoolean(p[6]));
    }
}
