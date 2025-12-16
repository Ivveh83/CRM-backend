package ivar.hogblom.crmbackend.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        name = "CustomerForContractComponents",
        description = "Lightweight customer representation used for contract-related UI components such as dropdowns."
)
public record CustomerForContractComponentsDto(

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
                description = "Swedish organization number of the customer.",
                example = "556677-8899"
        )
        String orgNo

) {
}
