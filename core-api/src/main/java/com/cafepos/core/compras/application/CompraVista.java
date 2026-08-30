package com.cafepos.core.compras.application;

import com.cafepos.core.compras.domain.Compra;
import com.cafepos.core.compras.domain.CompraDetalleItemVista;

import java.util.List;

/** GET /compras/{id} — proveedorNombre/usuarioNombre aplanados, mismo criterio que CompraListadoItem. */
public record CompraVista(Compra compra, String proveedorNombre, String usuarioNombre, List<CompraDetalleItemVista> items) {
}
