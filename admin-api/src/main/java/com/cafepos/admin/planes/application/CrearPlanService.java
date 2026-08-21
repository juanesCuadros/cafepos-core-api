package com.cafepos.admin.planes.application;

import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CrearPlanService {

    private final PlanRepository planRepository;

    public CrearPlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan ejecutar(String nombre, String descripcion, BigDecimal precioMensual,
                          Integer limiteUsuarios, int diasPrueba) {
        return planRepository.save(new Plan(nombre, descripcion, precioMensual, limiteUsuarios, diasPrueba));
    }
}
