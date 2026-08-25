package com.cafepos.core.shared.criptografia;

/**
 * NUNCA debe llevar la llave de cifrado ni el valor (plano o cifrado) en su
 * mensaje — ver FactusCredencialesCryptoService, que solo pasa textos fijos
 * a este constructor, nunca los datos que fallaron.
 */
public class CifradoException extends RuntimeException {

    CifradoException(String mensaje) {
        super(mensaje);
    }

    CifradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
