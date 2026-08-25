package com.cafepos.core.shared.criptografia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

/**
 * @Convert(converter = FactusCredencialAttributeConverter.class) explicito en cada campo — NUNCA autoApply, para
 * no cifrar columnas de otro tipo por accidente. Bean de Spring (constructor injection) para que Hibernate
 * resuelva FactusCredencialesCryptoService via su integracion con el bean container de Spring en vez de
 * instanciar el converter con reflection pura — asi la llave sigue viniendo unicamente de la property
 * cafepos.factus.encryption-key, nunca hardcodeada aca ni duplicada.
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
