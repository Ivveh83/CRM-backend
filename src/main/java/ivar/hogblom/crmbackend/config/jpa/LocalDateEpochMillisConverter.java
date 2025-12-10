package ivar.hogblom.crmbackend.config.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Converter(autoApply = false)
public class LocalDateEpochMillisConverter
        implements AttributeConverter<LocalDate, Long> {

    private static final ZoneId ZONE = ZoneId.of("UTC");
    // ✅ Rekommenderas för CRM-system

    @Override
    public Long convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) return null;

        return attribute
                .atStartOfDay(ZONE)
                .toInstant()
                .toEpochMilli();
    }

    @Override
    public LocalDate convertToEntityAttribute(Long dbData) {
        if (dbData == null) return null;

        return Instant
                .ofEpochMilli(dbData)
                .atZone(ZONE)
                .toLocalDate();
    }
}
