package ivar.hogblom.crmbackend.dto.reseller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        name = "ResellerForContractComponents",
        description = "Lightweight reseller representation used for contract-related UI components such as dropdowns."
)
public record ResellerForContractComponentsDto(

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
                description = "Swedish organization number of the reseller.",
                example = "556677-8899"
        )
        String orgNo,

        @Schema(
                description = "Whether the reseller is active.",
                example = "true"
        )
        boolean active

) {
}
