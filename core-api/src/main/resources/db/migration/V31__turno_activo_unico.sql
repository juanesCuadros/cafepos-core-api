-- Mismo problema que ya se arregló para mesas (V29__pedido_mesa_activo_unico.sql):
-- TurnoService.iniciar() hace un chequeo "check-then-act" sin bloqueo
-- (buscarActivoPorUsuario().isPresent() y despues el INSERT) — dos requests
-- casi simultaneas (doble clic, reintento de red, dos pestañas) pueden pasar
-- el chequeo antes de que ninguna confirme su turno, dejando dos turnos
-- 'activos' (hora_fin IS NULL) para el mismo usuario. Nunca se aplico acá el
-- mismo fix que a mesas, ver FASE1_AUDITORIA_OPERACION_CAJA_PRODUCTOS.md 2.3.1
-- (el frontend de TurnoPage.tsx incluso asume que esta proteccion ya existia).

-- Paso 1: cerrar cualquier turno duplicado que ya haya quedado activo antes
-- de este fix -- sin esto, crear el indice de abajo fallaria de inmediato
-- contra cualquier usuario que ya tenga el problema. Se deja abierto el mas
-- reciente por usuario, se cierran los demas con la hora de este deploy
-- (no hay forma de saber la hora real de cierre de un turno que nunca se
-- cerro correctamente).
WITH duplicados AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY tenant_id, usuario_id ORDER BY hora_inicio DESC) AS orden
    FROM turno
    WHERE usuario_id IS NOT NULL AND hora_fin IS NULL
)
UPDATE turno
SET hora_fin = now()
WHERE id IN (SELECT id FROM duplicados WHERE orden > 1);

-- Paso 2: indice unico parcial -- garantiza a nivel de base de datos que
-- nunca puede coexistir mas de un turno activo (hora_fin IS NULL) para el
-- mismo usuario, sin importar que tan rapido lleguen dos requests
-- concurrentes. El chequeo previo en TurnoService.iniciar() sigue siendo la
-- primera linea de defensa para dar un error legible (TurnoYaActivoException);
-- este indice es la garantia real que no depende de que ese chequeo gane la
-- carrera.
CREATE UNIQUE INDEX idx_turno_activo_unico
    ON turno (tenant_id, usuario_id)
    WHERE usuario_id IS NOT NULL AND hora_fin IS NULL;
