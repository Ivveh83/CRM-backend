package ivar.hogblom.crmbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SubscriptionRequestDto(
        @NotNull String name,
        String category,
        String description,
        String serviceLevel,
        @NotNull Double pricePerMonth,
        @NotNull Integer contractLength,
        Integer renewalPeriod,
        Boolean active,
        String supportContact,
        LocalDate createdAt,
        String notes
) {}
