package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.MenuDigitalConfig;
import com.cafepos.core.restaurante.domain.MenuDigitalRepository;
import com.cafepos.core.restaurante.domain.MenuDigitalVista;
import com.cafepos.core.shared.tenant.Tenant;
import com.cafepos.core.shared.tenant.TenantContext;
import com.cafepos.core.shared.tenant.TenantNoEncontradoException;
import com.cafepos.core.shared.tenant.TenantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuDigitalService {

    private static final String MENSAJE_ACTIVADO = "Menú digital activado";
    private static final String MENSAJE_DESACTIVADO = "Menú digital desactivado";

    private final MenuDigitalRepository menuDigitalRepository;
    private final TenantRepository tenantRepository;
    private final String tenantBaseDomain;

    public MenuDigitalService(MenuDigitalRepository menuDigitalRepository, TenantRepository tenantRepository,
                               @Value("${cafepos.tenant.base-domain}") String tenantBaseDomain) {
        this.menuDigitalRepository = menuDigitalRepository;
        this.tenantRepository = tenantRepository;
        this.tenantBaseDomain = tenantBaseDomain;
    }

    @Transactional(readOnly = true)
    public MenuDigitalVista obtener() {
        MenuDigitalConfig config = menuDigitalRepository.buscarPorTenantActual().orElse(null);
        boolean activo = config != null && config.isActivo();
        String urlPublica = config != null && config.getUrlPublica() != null ? config.getUrlPublica()
                : construirUrlPublica();
        return new MenuDigitalVista(activo, urlPublica, QrCodeGenerator.generarDataUri(urlPublica), null);
    }

    @Transactional
    public MenuDigitalVista actualizar(boolean activo) {
        MenuDigitalConfig config = menuDigitalRepository.buscarPorTenantActual()
                .orElseGet(() -> new MenuDigitalConfig(TenantContext.getCurrentTenantId()));
        if (activo) {
            config.activar(construirUrlPublica());
        } else {
            config.desactivar();
        }
        config = menuDigitalRepository.guardar(config);
        String mensaje = activo ? MENSAJE_ACTIVADO : MENSAJE_DESACTIVADO;
        return new MenuDigitalVista(config.isActivo(), config.getUrlPublica(),
                QrCodeGenerator.generarDataUri(config.getUrlPublica()), mensaje);
    }

    private String construirUrlPublica() {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(TenantNoEncontradoException::new);
        return "https://" + tenant.getSlug() + "." + tenantBaseDomain + "/menu-publico";
    }
}
