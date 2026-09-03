package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.FacturacionDianResolucion;
import com.cafepos.admin.negocios.domain.FacturacionDianResolucionRepository;
import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantNoEncontradoException;
import com.cafepos.admin.negocios.domain.TenantRepository;
import com.cafepos.admin.negocios.domain.UsuarioRepository;
import com.cafepos.admin.negocios.infrastructure.web.NegocioDetalleResponse;
import com.cafepos.admin.planes.domain.Plan;
import com.cafepos.admin.planes.domain.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalleNegocioService {

    private final TenantRepository tenantRepository;
    private final RestauranteRepository restauranteRepository;
    private final PlanRepository planRepository;
    private final FacturacionDianResolucionRepository facturacionDianRepository;
    private final UsuarioRepository usuarioRepository;

    public DetalleNegocioService(TenantRepository tenantRepository,
                                 RestauranteRepository restauranteRepository,
                                 PlanRepository planRepository,
                                 FacturacionDianResolucionRepository facturacionDianRepository,
                                 UsuarioRepository usuarioRepository) {
        this.tenantRepository = tenantRepository;
        this.restauranteRepository = restauranteRepository;
        this.planRepository = planRepository;
        this.facturacionDianRepository = facturacionDianRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public NegocioDetalleResponse ejecutar(Integer tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(TenantNoEncontradoException::new);

        Restaurante restaurante = restauranteRepository.findByTenantId(tenantId)
                .orElse(null);

        Plan plan = planRepository.findById(tenant.getPlanId())
                .orElse(null);

        FacturacionDianResolucion dian = facturacionDianRepository.buscarVigentePorTenant(tenantId)
                .orElse(null);

        long totalUsuarios = usuarioRepository.countByTenantId(tenantId);

        NegocioDetalleResponse.RestauranteInfo restInfo = restaurante != null
                ? new NegocioDetalleResponse.RestauranteInfo(
                        restaurante.getNombreNegocio(),
                        restaurante.getNit(),
                        restaurante.getDireccion(),
                        restaurante.getDepartamento(),
                        restaurante.getCiudad(),
                        restaurante.getTelefono(),
                        restaurante.getCorreo())
                : new NegocioDetalleResponse.RestauranteInfo(tenant.getSlug(), null, null, null, null, null, null);

        NegocioDetalleResponse.PlanInfo planInfo = plan != null
                ? new NegocioDetalleResponse.PlanInfo(
                        plan.getId(),
                        plan.getNombre(),
                        plan.getPrecioMensual(),
                        plan.getLimiteUsuarios(),
                        plan.getDiasPrueba())
                : null;

        NegocioDetalleResponse.FacturacionDianInfo dianInfo = dian != null
                ? new NegocioDetalleResponse.FacturacionDianInfo(
                        true,
                        dian.getAmbiente(),
                        dian.getPrefijo(),
                        dian.getEstado(),
                        dian.getFechaVencimiento())
                : new NegocioDetalleResponse.FacturacionDianInfo(false, null, null, null, null);

        return new NegocioDetalleResponse(
                tenant.getId(),
                tenant.getSlug(),
                tenant.getEstado(),
                tenant.getFechaRegistro(),
                tenant.getFechaProximaFacturacion(),
                tenant.getSuperadminAprobadorId(),
                restInfo,
                planInfo,
                dianInfo,
                totalUsuarios
        );
    }
}
