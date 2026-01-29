package ivar.hogblom.crmbackend.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(
        name = "SubscriptionRequest",
        description = "Request payload for creating or updating a subscription."
)
public record SubscriptionRequestDto(

        @Schema(
                description = "Name of the subscription.",
                example = "Premium Plan",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
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
                example = "199.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Double pricePerMonth,

        @Schema(
                description = "Contract length in months.",
                example = "12",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Integer contractLength,

        @Schema(
                description = "Renewal period in months. If null, defaults to contract length.",
                example = "12"
        )
        Integer renewalPeriod,

        @Schema(
                description = "Whether the subscription is active.",
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
                example = "2025-01-01",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        LocalDate createdAt,

        @Schema(
                description = "Additional internal notes about the subscription.",
                example = "Includes priority support"
        )
        String notes

) {}
