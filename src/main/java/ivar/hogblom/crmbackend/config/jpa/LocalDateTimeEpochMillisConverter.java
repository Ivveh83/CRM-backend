package ivar.hogblom.crmbackend.config.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Converter(autoApply = false)
public class LocalDateTimeEpochMillisConverter
        implements AttributeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime attribute) {
        if (attribute == null) return null;

        // LocalDateTime → epoch millis (UTC)
        return attribute
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        if (dbData == null) return null;

        // epoch millis → LocalDateTime (UTC)
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dbData),
                ZoneOffset.UTC
        );
    }
}
