package com.cafepos.admin.planes.application;

import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanNoEncontradoException;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarEstadoPlanService {

    private final PlanRepository planRepository;

    public CambiarEstadoPlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan ejecutar(Integer id, String nuevoEstado) {
        Plan plan = planRepository.findById(id).orElseThrow(PlanNoEncontradoException::new);
        plan.cambiarEstado(nuevoEstado);
        return plan;
    }
}
