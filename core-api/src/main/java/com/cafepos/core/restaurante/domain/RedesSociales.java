package com.cafepos.core.restaurante.domain;

/**
 * Claves conocidas y fijas (instagram, facebook, whatsapp) en vez de un
 * Map&lt;String,Object&gt; — mas limpio: el tipo mismo garantiza que no
 * puede llegar una clave rara a la base (Jackson simplemente no tiene
 * donde ponerla), sin necesitar un validador manual de claves permitidas.
 */
public record RedesSociales(String instagram, String facebook, String whatsapp) {
}
