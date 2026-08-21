package com.cafepos.admin.planes.application;

import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanNoEncontradoException;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EditarPlanService {

    private final PlanRepository planRepository;

    public EditarPlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan ejecutar(Integer id, String nombre, String descripcion, BigDecimal precioMensual,
                          Integer limiteUsuarios, int diasPrueba) {
        Plan plan = planRepository.findById(id).orElseThrow(PlanNoEncontradoException::new);
        plan.actualizar(nombre, descripcion, precioMensual, limiteUsuarios, diasPrueba);
        return plan;
    }
}
