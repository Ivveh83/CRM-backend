package ivar.hogblom.crmbackend.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        name = "SubscriptionForContractComponents",
        description = "Lightweight subscription representation used for contract-related UI components such as dropdowns."
)
public record SubscriptionForContractComponentsDto(

        @Schema(
                description = "Unique identifier of the subscription.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Name of the subscription.",
                example = "Premium Plan"
        )
        String name,

        @Schema(
                description = "Contract length in months for the subscription.",
                example = "12"
        )
        Integer contractLength,

        @Schema(
                description = "Renewal period in months for the subscription.",
                example = "12"
        )
        Integer renewalPeriod,

        @Schema(
                description = "Whether the subscription is active.",
                example = "true"
        )
        Boolean active,

        @Schema(
                description = "Monthly price of the subscription in SEK.",
                example = "199.00"
        )
        Double pricePerMonth

) {}
