package com.cafepos.core.restaurante.application;

import com.cafepos.core.productosmenu.application.ProductoService;
import com.cafepos.core.productosmenu.domain.ProductoPublico;
import com.cafepos.core.restaurante.domain.MenuDigitalConfig;
import com.cafepos.core.restaurante.domain.MenuDigitalRepository;
import com.cafepos.core.restaurante.domain.MenuPublicoCategoria;
import com.cafepos.core.restaurante.domain.MenuPublicoProducto;
import com.cafepos.core.restaurante.domain.MenuPublicoVista;
import com.cafepos.core.restaurante.domain.Restaurante;
import com.cafepos.core.restaurante.domain.RestauranteRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Llama directamente a ProductoService (com.cafepos.core.productosmenu) —
 * "llamada directa sincrona" entre modulos para el mismo request, ver
 * package-info.java de este modulo y de productosmenu.
 */
@Service
public class MenuPublicoService {

    private final RestauranteRepository restauranteRepository;
    private final MenuDigitalRepository menuDigitalRepository;
    private final ProductoService productoService;

    public MenuPublicoService(RestauranteRepository restauranteRepository, MenuDigitalRepository menuDigitalRepository,
                              ProductoService productoService) {
        this.restauranteRepository = restauranteRepository;
        this.menuDigitalRepository = menuDigitalRepository;
        this.productoService = productoService;
    }

    /** Vacio = "no disponible" (menu desactivado, o no configurado) — el controller lo traduce a 404 uniforme. */
    @Transactional(readOnly = true)
    public Optional<MenuPublicoVista> obtener() {
        if (TenantContext.getCurrentTenantId() == null) {
            // Sin tenant resuelto (host sin subdominio valido) no hay RLS que aplicar - ni intentar la query.
            return Optional.empty();
        }
        boolean activo = menuDigitalRepository.buscarPorTenantActual().map(MenuDigitalConfig::isActivo).orElse(false);
        if (!activo) {
            return Optional.empty();
        }
        Optional<Restaurante> restaurante = restauranteRepository.buscarPorTenantActual();
        if (restaurante.isEmpty()) {
            return Optional.empty();
        }

        List<ProductoPublico> productos = productoService.listarVisiblesParaMenuPublico();
        Map<String, List<MenuPublicoProducto>> porCategoria = new LinkedHashMap<>();
        for (ProductoPublico p : productos) {
            porCategoria.computeIfAbsent(p.categoriaNombre(), k -> new ArrayList<>())
                    .add(new MenuPublicoProducto(p.nombre(), p.descripcion(), p.precioVenta(), p.imagen()));
        }
        List<MenuPublicoCategoria> categorias = porCategoria.entrySet().stream()
                .map(e -> new MenuPublicoCategoria(e.getKey(), e.getValue()))
                .toList();

        Restaurante r = restaurante.get();
        return Optional.of(new MenuPublicoVista(r.getNombreNegocio(), r.getLogoUrl(), categorias));
    }
}
