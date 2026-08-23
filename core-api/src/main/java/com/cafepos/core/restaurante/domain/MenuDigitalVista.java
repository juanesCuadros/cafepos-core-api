package com.cafepos.core.restaurante.domain;

/** Vista de GET/PATCH /restaurante/menu-digital — mensaje solo viaja en la respuesta del PATCH. */
public record MenuDigitalVista(boolean activo, String urlPublica, String qrImageDataUri, String mensaje) {
}
