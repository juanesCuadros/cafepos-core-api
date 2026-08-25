package com.cafepos.admin.shared.criptografia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

/**
 * COPIA INTENCIONAL de com.cafepos.core.shared.criptografia.FactusCredencialAttributeConverter
 * en core-api — ver Javadoc de FactusCredencialesCryptoService (misma nota
 * de sincronizacion manual). @Convert explicito en cada campo — NUNCA
 * autoApply, para no cifrar columnas de otro tipo por accidente.
 */
@Converter
@Component
public class FactusCredencialAttributeConverter implements AttributeConverter<String, String> {

    private final FactusCredencialesCryptoService cryptoService;

    public FactusCredencialAttributeConverter(FactusCredencialesCryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public String convertToDatabaseColumn(String atributo) {
        return cryptoService.encrypt(atributo);
    }

    @Override
    public String convertToEntityAttribute(String columna) {
        return cryptoService.decrypt(columna);
    }
}
