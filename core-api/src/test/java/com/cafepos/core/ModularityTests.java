package com.cafepos.core;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Este test es el corazón de la decisión "Spring Modulith para forzar
 * automáticamente los límites entre módulos" (aprobada en el diseño de
 * arquitectura). Si un módulo llega a acceder a las clases internas
 * (domain/application/infrastructure) de otro módulo sin pasar por su
 * package-info.java expuesto, este test FALLA el build.
 *
 * No se debe deshabilitar ni "arreglar" ignorando la excepción — si falla,
 * es señal de que dos módulos se están acoplando de una forma que
 * rompe el diseño de límites que se aprobó explícitamente.
 */
class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(CoreApiApplication.class);

    @Test
    void verificaLimitesEntreModulos() {
        modules.verify();
    }

    @Test
    void imprimeEstructuraDeModulos() {
        modules.forEach(System.out::println);
    }

    @Test
    void generaDocumentacion() {
        // Genera diagramas PlantUML + Asciidoc de la estructura de módulos
        // en target/spring-modulith-docs/ — útil para mantener actualizada
        // la visión general del sistema sin escribirla a mano.
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
