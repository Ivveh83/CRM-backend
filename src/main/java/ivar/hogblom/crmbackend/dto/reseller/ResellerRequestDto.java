package ivar.hogblom.crmbackend.dto.reseller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(
        name = "ResellerRequest",
        description = "Request payload for creating or updating a reseller."
)
public record ResellerRequestDto(

        @Schema(
                description = "Name of the reseller.",
                example = "Nordic Reseller AB",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String name,

        @Schema(
                description = "Swedish organization number of the reseller.",
                example = "556677-8899",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        String orgNo,

        @Schema(
                description = "Whether the reseller is active.",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        boolean active,

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
                example = "2025-01-10",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        LocalDate createdAt

) {
}
