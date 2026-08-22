package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.ComboGrupo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ComboGrupoJpaRepository extends TenantAwareRepository<ComboGrupo, Integer> {

    /** Scopeado por combo_id a proposito — un grupo_id valido de OTRO combo del mismo tenant no debe matchear aca. */
    Optional<ComboGrupo> findByIdAndComboId(Integer id, Integer comboId);

    /**
     * Flat (grupo + producto) en una sola query en vez de N+1 por grupo —
     * LEFT JOIN para no perder grupos sin productos todavia. Se agrupa en
     * ComboRepositoryAdapter.gruposDe.
     */
    @Query(value = "SELECT cg.id AS grupo_id, cg.nombre AS grupo_nombre, p.id AS producto_id, "
            + "p.nombre AS producto_nombre "
            + "FROM combo_grupo cg "
            + "LEFT JOIN combo_grupo_producto cgp ON cgp.combo_grupo_id = cg.id "
            + "LEFT JOIN producto p ON p.id = cgp.producto_id "
            + "WHERE cg.combo_id = :comboId "
            + "ORDER BY cg.id, p.nombre", nativeQuery = true)
    List<ComboGrupoProductoRow> gruposDe(@Param("comboId") Integer comboId);
}
