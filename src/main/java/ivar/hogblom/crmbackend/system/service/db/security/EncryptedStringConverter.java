package ivar.hogblom.crmbackend.system.service.db.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class EncryptedStringConverter
        implements AttributeConverter<String, String> {

    private static CryptoService crypto;

    @Autowired
    public void setCryptoService(CryptoService service) {
        EncryptedStringConverter.crypto = service;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return crypto.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return crypto.decrypt(dbData);
    }
}
