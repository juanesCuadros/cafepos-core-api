-- VARCHAR(50) no alcanza para los 7 dias en CSV
-- ("lunes,martes,miercoles,jueves,viernes,sabado,domingo" = 52 caracteres).
ALTER TABLE promocion ALTER COLUMN dias_semana TYPE VARCHAR(100);
