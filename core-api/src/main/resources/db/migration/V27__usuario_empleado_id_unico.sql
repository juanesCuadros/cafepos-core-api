-- Indice parcial: permite multiples usuario.empleado_id NULL (la mayoria),
-- pero exige unicidad entre los no-nulos - a lo sumo un usuario por
-- empleado (ver com.cafepos.core.personal, GET /empleados/{id}
-- usuario_asociado). Verificado sin duplicados existentes antes de aplicar.
CREATE UNIQUE INDEX idx_usuario_empleado_id_unico ON usuario(empleado_id)
    WHERE empleado_id IS NOT NULL;
