package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record SubscriptionResponseDto(
        UUID id,
        String name,
        String category,
        String description,
        String serviceLevel,
        Double pricePerMonth,
        Integer contractLength,
        Integer renewalPeriod,
        Boolean active,
        String supportContact,
        LocalDate createdAt,
        String notes
) {}
