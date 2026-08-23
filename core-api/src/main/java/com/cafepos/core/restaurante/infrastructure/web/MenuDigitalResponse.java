package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MenuDigitalVista;
import com.fasterxml.jackson.annotation.JsonInclude;

/** "mensaje" solo viaja en la respuesta del PATCH (activar/desactivar), no en el GET. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuDigitalResponse(boolean activo, String urlPublica, String qrImageUrl, String mensaje) {

    public static MenuDigitalResponse de(MenuDigitalVista vista) {
        return new MenuDigitalResponse(vista.activo(), vista.urlPublica(), vista.qrImageDataUri(), vista.mensaje());
    }
}
