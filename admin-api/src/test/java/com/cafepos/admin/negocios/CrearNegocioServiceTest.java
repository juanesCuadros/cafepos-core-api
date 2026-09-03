package com.cafepos.admin.negocios;

import com.cafepos.admin.negocios.application.CrearNegocioService;
import com.cafepos.admin.negocios.application.NegocioCreado;
import com.cafepos.admin.negocios.domain.ConfiguracionSistemaRepository;
import com.cafepos.admin.negocios.domain.MetodoPagoRepository;
import com.cafepos.admin.negocios.domain.PermisoRepository;
import com.cafepos.admin.negocios.domain.PlanNoExisteException;
import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.Rol;
import com.cafepos.admin.negocios.domain.RolPermisoRepository;
import com.cafepos.admin.negocios.domain.RolRepository;
import com.cafepos.admin.negocios.domain.SlugYaExisteException;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantPermisoConfigRepository;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.negocios.domain.TenantRolConfigRepository;
import com.cafepos.admin.negocios.domain.UsuarioRepository;
import com.cafepos.admin.negocios.infrastructure.TemporaryPasswordGenerator;
import com.cafepos.admin.negocios.infrastructure.persistence.MatrizPermisosDefaultLoader;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearNegocioServiceTest {

    @Mock private TenantRepository tenantRepository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PermisoRepository permisoRepository;
    @Mock private RolPermisoRepository rolPermisoRepository;
    @Mock private TenantPermisoConfigRepository tenantPermisoConfigRepository;
    @Mock private TenantRolConfigRepository tenantRolConfigRepository;
    @Mock private ConfiguracionSistemaRepository configuracionSistemaRepository;
    @Mock private MetodoPagoRepository metodoPagoRepository;
    @Mock private SuscripcionesHistorialRepository suscripcionesHistorialRepository;
    @Mock private PlanRepository planRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MatrizPermisosDefaultLoader matrizPermisosDefaultLoader;
    @Mock private TemporaryPasswordGenerator temporaryPasswordGenerator;

    @InjectMocks
    private CrearNegocioService service;

    @Test
    void ejecutar_datosValidos_creaTenantYActualizaNombreNegocio() {
        Plan plan = new Plan("Plan Pro", "Desc", BigDecimal.valueOf(99000), 5, 14);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(tenantRepository.existsBySlug("cafe-san-alberto")).thenReturn(false);

        Tenant tenantGuardado = new Tenant(1, 1, "cafe-san-alberto", Tenant.ESTADO_PRUEBA, null);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenantGuardado);

        Restaurante restauranteTrigger = new Restaurante(tenantGuardado.getId(), "cafe-san-alberto");
        when(restauranteRepository.findByTenantId(tenantGuardado.getId())).thenReturn(Optional.of(restauranteTrigger));

        Rol rolJefe = new Rol(1, "Jefe");
        when(rolRepository.findAll()).thenReturn(List.of(rolJefe));
        when(temporaryPasswordGenerator.generar()).thenReturn("TempPass12345");
        when(passwordEncoder.encode("TempPass12345")).thenReturn("$2a$encoded");
        when(permisoRepository.findAll()).thenReturn(Collections.emptyList());
        when(matrizPermisosDefaultLoader.filas()).thenReturn(Collections.emptyList());

        NegocioCreado creado = service.ejecutar("Café San Alberto", "cafe-san-alberto", 1,
                "jefe@sanalberto.com", "Juan Perez", 1);

        assertNotNull(creado);
        assertEquals("cafe-san-alberto", creado.slug());
        assertEquals("jefe@sanalberto.com", creado.correoJefe());
        assertEquals("TempPass12345", creado.passwordTemporal());

        // Verificación CRÍTICA: se actualizó el nombre real del restaurante en lugar de dejar el slug
        assertEquals("Café San Alberto", restauranteTrigger.getNombreNegocio());
        verify(restauranteRepository).save(restauranteTrigger);
    }

    @Test
    void ejecutar_slugDuplicado_lanzaSlugYaExisteException() {
        Plan plan = new Plan("Plan Pro", "Desc", BigDecimal.valueOf(99000), 5, 0);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(tenantRepository.existsBySlug("slug-existente")).thenReturn(true);

        assertThrows(SlugYaExisteException.class, () ->
                service.ejecutar("Negocio", "slug-existente", 1, "jefe@mail.com", "Jefe", 1));
    }

    @Test
    void ejecutar_planInexistente_lanzaPlanNoExisteException() {
        when(planRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(PlanNoExisteException.class, () ->
                service.ejecutar("Negocio", "slug", 999, "jefe@mail.com", "Jefe", 1));
    }
}
