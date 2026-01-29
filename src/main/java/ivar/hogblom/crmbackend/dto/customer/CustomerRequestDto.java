package ivar.hogblom.crmbackend.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(
        name = "CustomerRequest",
        description = "Request payload for creating or updating a customer."
)
public record CustomerRequestDto(

        @Schema(
                description = "Company name of the customer.",
                example = "Acme AB",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String companyName,

        @Schema(
                description = "Swedish organization number.",
                example = "556677-8899",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String orgNo,

        @Schema(
                description = "Primary contact person's name.",
                example = "Anna Andersson"
        )
        String contactName,

        @Schema(
                description = "Primary contact email address.",
                example = "anna.andersson@acme.se"
        )
        String contactEmail,

        @Schema(
                description = "Primary contact phone number.",
                example = "+46 70 123 45 67"
        )
        String contactPhone,

        @Schema(
                description = "Street address of the customer.",
                example = "Storgatan 1"
        )
        String address,

        @Schema(
                description = "City where the customer is located.",
                example = "Stockholm"
        )
        String city,

        @Schema(
                description = "ZIP or postal code.",
                example = "111 22"
        )
        String zipCode,

        @Schema(
                description = "Country where the customer is located.",
                example = "Sweden"
        )
        String country,

        @Schema(
                description = "Industry classification of the customer.",
                example = "Software"
        )
        String industry,

        @Schema(
                description = "Type or segment of the customer.",
                example = "Enterprise"
        )
        String customerType,

        @Schema(
                description = "Date when the customer was created.",
                example = "2025-01-15"
        )
        LocalDate createdAt,

        @Schema(
                description = "Additional internal notes about the customer.",
                example = "Important strategic customer"
        )
        String notes

) {}
