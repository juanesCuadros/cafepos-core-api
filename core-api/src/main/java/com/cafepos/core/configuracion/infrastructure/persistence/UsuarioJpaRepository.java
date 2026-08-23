package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface UsuarioJpaRepository extends TenantAwareRepository<Usuario, Integer> {

    @Query(value = "SELECT u.id AS id, u.nombre AS nombre, u.correo AS correo, u.rol_id AS rol_id, r.nombre AS rol, "
            + "u.empleado_id AS empleado_id, e.nombre AS empleado_asociado, u.estado AS estado "
            + "FROM usuario u JOIN rol r ON r.id = u.rol_id LEFT JOIN empleado e ON e.id = u.empleado_id "
            + "WHERE (:rolId IS NULL OR u.rol_id = :rolId) AND (:estado IS NULL OR u.estado = :estado) "
            + "ORDER BY u.nombre", nativeQuery = true)
    List<UsuarioRow> listar(@Param("rolId") Integer rolId, @Param("estado") String estado);

    @Query(value = "SELECT u.id AS id, u.nombre AS nombre, u.correo AS correo, u.rol_id AS rol_id, r.nombre AS rol, "
            + "u.empleado_id AS empleado_id, e.nombre AS empleado_asociado, u.estado AS estado "
            + "FROM usuario u JOIN rol r ON r.id = u.rol_id LEFT JOIN empleado e ON e.id = u.empleado_id "
            + "WHERE u.id = :id", nativeQuery = true)
    Optional<UsuarioRow> detalleDe(@Param("id") Integer id);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE rol_id = :rolId AND estado = 'activo'", nativeQuery = true)
    long contarActivosPorRol(@Param("rolId") Integer rolId);
}
