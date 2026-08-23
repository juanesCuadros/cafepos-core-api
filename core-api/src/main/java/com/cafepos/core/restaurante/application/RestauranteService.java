package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.RedesSociales;
import com.cafepos.core.restaurante.domain.Restaurante;
import com.cafepos.core.restaurante.domain.RestauranteNoConfiguradoException;
import com.cafepos.core.restaurante.domain.RestauranteRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional(readOnly = true)
    public Restaurante obtener() {
        return restauranteRepository.buscarPorTenantActual().orElseThrow(RestauranteNoConfiguradoException::new);
    }

    @Transactional
    public Restaurante actualizar(String nombreNegocio, JsonNullable<String> nit, JsonNullable<String> direccion,
                                   JsonNullable<String> departamento, JsonNullable<String> ciudad,
                                   JsonNullable<String> pais, JsonNullable<String> telefono,
                                   JsonNullable<String> correo, JsonNullable<String> telefonoRepresentante,
                                   JsonNullable<String> logoUrl, JsonNullable<RedesSociales> redesSociales,
                                   JsonNullable<String> descripcion) {
        Restaurante restaurante = obtener();
        restaurante.actualizar(nombreNegocio, nit, direccion, departamento, ciudad, pais, telefono, correo,
                telefonoRepresentante, logoUrl, redesSociales, descripcion);
        return restauranteRepository.guardar(restaurante);
    }
}
