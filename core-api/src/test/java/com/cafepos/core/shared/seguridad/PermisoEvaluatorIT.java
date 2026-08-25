package com.cafepos.core.shared.seguridad;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cafepos.core.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion real contra el tenant de prueba "cafe-de-prueba"
 * (tenant_id=6 en la base dev) — requiere Postgres dev levantado (docker
 * compose up -d) con el catalogo de permisos y rol_permiso ya sembrados
 * para ese tenant (ver V2__catalogo_permisos.sql y su matriz por defecto).
 *
 * No hay request HTTP de por medio (no pasa por TenantFilter), asi que
 * TenantContext se fija a mano antes de cada hasPermission(), igual que lo
 * haria el filtro real — sin eso, TenantAwareJpaTransactionManager no tiene
 * de donde sacar el tenant_id para el SET LOCAL que activa Row-Level
 * Security sobre rol_permiso, y la query fallaria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
class PermisoEvaluatorIT {

    private static final Integer TENANT_CAFE_DE_PRUEBA = 6;
    private static final Integer ROL_JEFE = 1;
    private static final Integer ROL_CAJERO = 3;

    @Autowired
    private PermisoEvaluator permisoEvaluator;

    @AfterEach
    void limpiarTenantContext() {
        TenantContext.clear();
    }

    @Test
    void jefeSiempreTieneAcceso_viaEsEditableFalse_noViaRolPermiso() {
        Authentication auth = autenticacion(ROL_JEFE);
        TenantContext.setCurrentTenantId(TENANT_CAFE_DE_PRUEBA);

        assertThat(permisoEvaluator.hasPermission(auth, "productos_menu.categorias", "crear")).isTrue();
    }

    @Test
    void cajeroNoTieneAccesoAReportes_exclusivoDeJefe() {
        Authentication auth = autenticacion(ROL_CAJERO);
        TenantContext.setCurrentTenantId(TENANT_CAFE_DE_PRUEBA);

        assertThat(permisoEvaluator.hasPermission(auth, "reportes.ventas", "ver")).isFalse();
    }

    @Test
    void cajeroSiTieneAccesoACobrarEnCaja() {
        Authentication auth = autenticacion(ROL_CAJERO);
        TenantContext.setCurrentTenantId(TENANT_CAFE_DE_PRUEBA);

        assertThat(permisoEvaluator.hasPermission(auth, "caja.pos", "cobrar")).isTrue();
    }

    @Test
    void moduloAccionInexistenteEnCatalogo_deniegaYLogueaWarn() {
        Logger logger = (Logger) LoggerFactory.getLogger(PermisoEvaluator.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            Authentication auth = autenticacion(ROL_CAJERO);
            TenantContext.setCurrentTenantId(TENANT_CAFE_DE_PRUEBA);

            boolean resultado = permisoEvaluator.hasPermission(
                    auth, "no_existe_test_claude.modulo", "no_existe_test_claude_accion");

            assertThat(resultado).isFalse();
            assertThat(appender.list).anyMatch(evento ->
                    evento.getLevel() == Level.WARN
                            && evento.getFormattedMessage().contains("no_existe_test_claude.modulo")
                            && evento.getFormattedMessage().contains("no_existe_test_claude_accion"));
        } finally {
            logger.detachAppender(appender);
        }
    }

    private Authentication autenticacion(Integer rolId) {
        AuthenticatedUsuario principal = new AuthenticatedUsuario(999, TENANT_CAFE_DE_PRUEBA, rolId, false);
        return new UsernamePasswordAuthenticationToken(principal, null, List.of());
    }
}
