package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.negocios.infrastructure.web.NegocioResumenResponse;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListarNegociosService {

    private final TenantRepository tenantRepository;
    private final RestauranteRepository restauranteRepository;
    private final PlanRepository planRepository;

    public ListarNegociosService(TenantRepository tenantRepository,
                                 RestauranteRepository restauranteRepository,
                                 PlanRepository planRepository) {
        this.tenantRepository = tenantRepository;
        this.restauranteRepository = restauranteRepository;
        this.planRepository = planRepository;
    }

    @Transactional(readOnly = true)
    public Page<NegocioResumenResponse> ejecutar(String query, String estado, Integer planId, Pageable pageable) {
        Page<Tenant> tenantsPage = tenantRepository.buscarConFiltros(query, estado, planId, pageable);

        Map<Integer, String> nombresPlanes = planRepository.findAll().stream()
                .collect(Collectors.toMap(Plan::getId, Plan::getNombre, (a, b) -> a));

        return tenantsPage.map(tenant -> {
            String nombreNegocio = restauranteRepository.findByTenantId(tenant.getId())
                    .map(Restaurante::getNombreNegocio)
                    .orElse(tenant.getSlug());
            String planNombre = nombresPlanes.getOrDefault(tenant.getPlanId(), "Desconocido");

            return new NegocioResumenResponse(
                    tenant.getId(),
                    tenant.getSlug(),
                    nombreNegocio,
                    tenant.getPlanId(),
                    planNombre,
                    tenant.getEstado(),
                    tenant.getFechaRegistro(),
                    tenant.getFechaProximaFacturacion()
            );
        });
    }
}
