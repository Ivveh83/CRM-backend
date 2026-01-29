package ivar.hogblom.crmbackend.dto.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(
        name = "ContractResponse",
        description = "Contract representation returned from the system."
)
public record ContractResponseDto(

        @Schema(
                description = "Unique identifier of the contract.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Customer associated with the contract."
        )
        CustomerForContractComponentsDto customer,

        @Schema(
                description = "Resellers associated with the contract."
        )
        List<ResellerForContractComponentsDto> resellers,

        @Schema(
                description = "Subscription types included in the contract."
        )
        List<SubscriptionForContractComponentsDto> subscriptionTypes,

        @Schema(
                description = "Whether the contract is open for renewal.",
                example = "true"
        )
        boolean status,

        @Schema(
                description = "Whether the contract is currently active.",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Contract start date (ISO-8601).",
                example = "2025-01-01"
        )
        LocalDate contractDate,

        @Schema(
                description = "Contract length in months.",
                example = "12"
        )
        Integer contractLengthMonths,

        @Schema(
                description = "List of renewal dates for the contract.",
                example = "[\"2026-01-01\", \"2027-01-01\"]"
        )
        List<LocalDate> renewalDates,

        @Schema(
                description = "Total monthly price of the contract in SEK.",
                example = "1999.00"
        )
        Double totalPricePerMonth,

        @Schema(
                description = "Contract due date (ISO-8601).",
                example = "2025-12-31"
        )
        LocalDate dueDate,

        @Schema(
                description = "Optional comment or internal note for the contract.",
                example = "Annual enterprise agreement"
        )
        String comment

) {}
