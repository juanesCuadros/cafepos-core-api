package com.cafepos.admin.negocios.infrastructure;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Password temporal para el Jefe al crear un negocio — 16 caracteres,
 * al menos uno de cada categoria (mayuscula, minuscula, digito, simbolo),
 * el resto relleno del pool completo y todo mezclado con SecureRandom para
 * no dejar la categoria fija en una posicion predecible.
 */
@Component
public class TemporaryPasswordGenerator {

    private static final int LONGITUD = 16;
    private static final String MAYUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITOS = "23456789";
    private static final String SIMBOLOS = "!@#$%^&*-_=+?";
    private static final String POOL = MAYUSCULAS + MINUSCULAS + DIGITOS + SIMBOLOS;

    private final SecureRandom random = new SecureRandom();

    public String generar() {
        List<Character> caracteres = new ArrayList<>(LONGITUD);
        caracteres.add(elegirDe(MAYUSCULAS));
        caracteres.add(elegirDe(MINUSCULAS));
        caracteres.add(elegirDe(DIGITOS));
        caracteres.add(elegirDe(SIMBOLOS));
        while (caracteres.size() < LONGITUD) {
            caracteres.add(elegirDe(POOL));
        }
        for (int i = caracteres.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Character temp = caracteres.get(i);
            caracteres.set(i, caracteres.get(j));
            caracteres.set(j, temp);
        }
        StringBuilder sb = new StringBuilder(LONGITUD);
        caracteres.forEach(sb::append);
        return sb.toString();
    }

    private char elegirDe(String pool) {
        return pool.charAt(random.nextInt(pool.length()));
    }
}
