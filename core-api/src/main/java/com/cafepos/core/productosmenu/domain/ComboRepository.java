package com.cafepos.core.productosmenu.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Combo (y sus grupos/productos anidados) — implementado en infrastructure.persistence. */
public interface ComboRepository {

    Combo guardar(Combo combo);

    Optional<Combo> buscarPorId(Integer id);

    List<ComboResumen> listar();

    void eliminar(Combo combo);

    List<ComboGrupoDetalle> gruposDe(Integer comboId);

    ComboGrupo guardarGrupo(ComboGrupo grupo);

    /** Busca por id Y combo_id a la vez — un grupo_id valido de OTRO combo del mismo tenant no debe matchear aca. */
    Optional<ComboGrupo> buscarGrupo(Integer comboId, Integer grupoId);

    void eliminarGrupo(ComboGrupo grupo);

    /** Lanza ComboGrupoProductoYaExisteException si la asociacion ya existe (UNIQUE(combo_grupo_id, producto_id)). */
    void agregarProducto(ComboGrupoProducto asociacion);

    Optional<ComboGrupoProducto> buscarAsociacion(Integer comboGrupoId, Integer productoId);

    void quitarProducto(ComboGrupoProducto asociacion);
}
