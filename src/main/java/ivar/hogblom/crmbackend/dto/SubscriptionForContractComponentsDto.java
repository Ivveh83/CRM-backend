package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SubscriptionForContractComponentsDto(
        UUID id,
        String name,
        Integer contractLength,
        Integer renewalPeriod,
        Boolean active,
        Integer pricePerMonth
) {}
