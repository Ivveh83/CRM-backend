package ivar.hogblom.crmbackend.dto.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(
        name = "ContractRequest",
        description = "Request payload for creating or updating a contract."
)
public record ContractRequestDto(

        @Schema(
                description = "List of subscription IDs included in the contract.",
                example = "[\"550e8400-e29b-41d4-a716-446655440000\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty
        List<@NotNull UUID> subscriptionIds,

        @Schema(
                description = "List of reseller IDs associated with the contract.",
                example = "[\"550e8400-e29b-41d4-a716-446655440001\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty
        List<@NotNull UUID> resellerIds,

        @Schema(
                description = "ID of the customer owning the contract.",
                example = "550e8400-e29b-41d4-a716-446655440002",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        UUID customerId,

        @Schema(
                description = "Contract start date (ISO-8601).",
                example = "2025-01-01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        LocalDate contractDate,

        @Schema(
                description = "Contract due date (ISO-8601).",
                example = "2025-12-31",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        LocalDate dueDate,

        @Schema(
                description = "Optional list of contract renewal dates.",
                example = "[\"2026-01-01\", \"2027-01-01\"]"
        )
        List<LocalDate> renewalDates,

        @Schema(
                description = "Total monthly price for the contract in SEK.",
                example = "1999.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Double totalPricePerMonth,

        @Schema(
                description = "Contract status flag (business-specific meaning).",
                example = "true"
        )
        boolean status,

        @Schema(
                description = "Whether the contract is active.",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Contract length in months.",
                example = "12",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Integer contractLengthMonths,

        @Schema(
                description = "Optional comment or internal note for the contract.",
                example = "Annual enterprise agreement"
        )
        String comment

) {}
