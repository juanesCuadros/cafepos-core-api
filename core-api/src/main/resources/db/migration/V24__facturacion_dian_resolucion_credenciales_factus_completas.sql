-- El OAuth de Factus (grant_type=password) exige client_id + client_secret
-- (ya persistidos) Y ADEMAS username + password de la cuenta Factus del
-- tenant - faltaban estas dos columnas para conectar la integracion real
-- (ver FacturacionDianResolucion, cifradas igual que client_id_factus/
-- client_secret_factus via FactusCredencialAttributeConverter).
ALTER TABLE facturacion_dian_resolucion ADD COLUMN username_factus VARCHAR(255);
ALTER TABLE facturacion_dian_resolucion ADD COLUMN password_factus VARCHAR(255);
