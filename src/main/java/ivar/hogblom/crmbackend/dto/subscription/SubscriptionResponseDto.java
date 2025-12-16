package ivar.hogblom.crmbackend.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Schema(
        name = "SubscriptionResponse",
        description = "Subscription representation returned from the system."
)
public record SubscriptionResponseDto(

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
                description = "Category of the subscription.",
                example = "Software"
        )
        String category,

        @Schema(
                description = "Detailed description of the subscription.",
                example = "Premium access with extended support"
        )
        String description,

        @Schema(
                description = "Service level associated with the subscription.",
                example = "Gold"
        )
        String serviceLevel,

        @Schema(
                description = "Monthly price of the subscription in SEK.",
                example = "199.00"
        )
        Double pricePerMonth,

        @Schema(
                description = "Contract length in months.",
                example = "12"
        )
        Integer contractLength,

        @Schema(
                description = "Renewal period in months.",
                example = "12"
        )
        Integer renewalPeriod,

        @Schema(
                description = "Whether the subscription is currently active.",
                example = "true"
        )
        Boolean active,

        @Schema(
                description = "Support contact information for the subscription.",
                example = "support@company.se"
        )
        String supportContact,

        @Schema(
                description = "Date when the subscription was created.",
                example = "2025-01-01"
        )
        LocalDate createdAt,

        @Schema(
                description = "Additional internal notes about the subscription.",
                example = "Includes priority support"
        )
        String notes

) {}
