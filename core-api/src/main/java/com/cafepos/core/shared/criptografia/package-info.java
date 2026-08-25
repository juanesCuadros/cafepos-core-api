/**
 * Cifrado simetrico AES-256-GCM de credenciales externas que el negocio
 * necesita releer en texto plano (a diferencia de un password de usuario,
 * que solo se hashea con BCrypt porque nunca se vuelve a leer — ver
 * shared.seguridad). Primer y unico uso actual: client_id_factus /
 * client_secret_factus en restaurante.domain.FacturacionDianResolucion.
 *
 * La llave sale de la property cafepos.factus.encryption-key (variable de
 * entorno en cualquier ambiente real, ver application.yml/application-dev.yml)
 * — nunca hardcodeada, mismo patron que cafepos.jwt.signing-key.
 */
package com.cafepos.core.shared.criptografia;
