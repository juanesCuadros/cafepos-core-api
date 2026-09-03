package com.cafepos.admin.planes;

import com.cafepos.admin.planes.application.CambiarEstadoPlanService;
import com.cafepos.admin.planes.application.CrearPlanService;
import com.cafepos.admin.planes.application.EditarPlanService;
import com.cafepos.admin.planes.application.ListarPlanesService;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanNoEncontradoException;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private CrearPlanService crearPlanService;

    @InjectMocks
    private EditarPlanService editarPlanService;

    @InjectMocks
    private CambiarEstadoPlanService cambiarEstadoPlanService;

    @InjectMocks
    private ListarPlanesService listarPlanesService;

    @Test
    void crear_datosValidos_guardaPlan() {
        when(planRepository.save(any(Plan.class))).thenAnswer(inv -> inv.getArgument(0));

        Plan plan = crearPlanService.ejecutar("Pro", "Plan Profesional", BigDecimal.valueOf(120000), 10, 14);

        assertNotNull(plan);
        assertEquals("Pro", plan.getNombre());
        assertEquals(BigDecimal.valueOf(120000), plan.getPrecioMensual());
        assertEquals(10, plan.getLimiteUsuarios());
        assertEquals(14, plan.getDiasPrueba());
        assertEquals(Plan.ESTADO_ACTIVO, plan.getEstado());
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void editar_planExistente_actualizaPropiedades() {
        Plan plan = new Plan("Basico", "Desc", BigDecimal.valueOf(50000), 2, 7);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Plan editado = editarPlanService.ejecutar(1, "Basico Plus", "Nueva Desc", BigDecimal.valueOf(60000), 3, 10);

        assertEquals("Basico Plus", editado.getNombre());
        assertEquals(BigDecimal.valueOf(60000), editado.getPrecioMensual());
        assertEquals(3, editado.getLimiteUsuarios());
        assertEquals(10, editado.getDiasPrueba());
    }

    @Test
    void cambiarEstado_inactivaPlan() {
        Plan plan = new Plan("Basico", "Desc", BigDecimal.valueOf(50000), 2, 7);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Plan inactivo = cambiarEstadoPlanService.ejecutar(1, Plan.ESTADO_INACTIVO);

        assertEquals(Plan.ESTADO_INACTIVO, inactivo.getEstado());
    }

    @Test
    void cambiarEstado_planInexistente_lanzaExcepcion() {
        when(planRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PlanNoEncontradoException.class, () ->
                cambiarEstadoPlanService.ejecutar(99, Plan.ESTADO_INACTIVO));
    }

    @Test
    void listar_retornaListaDePlanes() {
        when(planRepository.findAll()).thenReturn(List.of(
                new Plan("Plan 1", "D1", BigDecimal.valueOf(100), 1, 0),
                new Plan("Plan 2", "D2", BigDecimal.valueOf(200), 5, 14)
        ));

        List<Plan> planes = listarPlanesService.ejecutar();

        assertEquals(2, planes.size());
    }
}
