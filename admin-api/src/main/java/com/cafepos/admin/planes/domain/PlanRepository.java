package com.cafepos.admin.planes.domain;

import java.util.List;
import java.util.Optional;

public interface PlanRepository {

    Plan save(Plan plan);

    Optional<Plan> findById(Integer id);

    List<Plan> findAll();
}
