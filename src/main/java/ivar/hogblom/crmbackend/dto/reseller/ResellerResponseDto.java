package ivar.hogblom.crmbackend.dto.reseller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Schema(
        name = "ResellerResponse",
        description = "Reseller representation returned from the system."
)
public record ResellerResponseDto(

        @Schema(
                description = "Unique identifier of the reseller.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Name of the reseller.",
                example = "Nordic Reseller AB"
        )
        String name,

        @Schema(
                description = "Whether the reseller is currently active.",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Swedish organization number of the reseller.",
                example = "556677-8899"
        )
        String orgNo,

        @Schema(
                description = "Street address of the reseller.",
                example = "Industrigatan 5"
        )
        String address,

        @Schema(
                description = "Contact email address.",
                example = "contact@nordicreseller.se"
        )
        String contactEmail,

        @Schema(
                description = "Contact telephone number.",
                example = "+46 8 123 45 67"
        )
        String contactTelephone,

        @Schema(
                description = "Invoice reference or billing contact.",
                example = "Invoice Dept."
        )
        String invoiceReference,

        @Schema(
                description = "Date when the reseller was created.",
                example = "2025-01-10"
        )
        LocalDate createdAt

) {}
