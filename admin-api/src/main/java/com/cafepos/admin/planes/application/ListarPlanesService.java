package com.cafepos.admin.planes.application;

import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarPlanesService {

    private final PlanRepository planRepository;

    public ListarPlanesService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /** Activos e inactivos: el superadmin necesita ver ambos para gestionarlos. */
    @Transactional(readOnly = true)
    public List<Plan> ejecutar() {
        return planRepository.findAll();
    }
}
