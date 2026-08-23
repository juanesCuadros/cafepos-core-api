-- redes_sociales viajaba como VARCHAR(500) sin estructura; pasa a JSONB para
-- mapear directo a un objeto {instagram, facebook, whatsapp} (Modulo 10.1).
-- Verificado antes de este archivo: las 3 filas existentes de restaurantes
-- tienen redes_sociales NULL, el ::JSONB no puede fallar con este dato.
ALTER TABLE restaurantes ALTER COLUMN redes_sociales TYPE JSONB
    USING redes_sociales::JSONB;
