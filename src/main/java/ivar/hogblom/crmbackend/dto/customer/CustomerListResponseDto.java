package ivar.hogblom.crmbackend.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(
        name = "CustomerListResponse",
        description = "Lightweight customer representation used for listing and overview purposes."
)
public record CustomerListResponseDto(

        @Schema(
                description = "Unique identifier of the customer.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Company name of the customer.",
                example = "Acme AB"
        )
        String companyName,

        @Schema(
                description = "Swedish organization number.",
                example = "556677-8899"
        )
        String orgNo,

        @Schema(
                description = "Primary contact person's name.",
                example = "Anna Andersson"
        )
        String contactName,

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
