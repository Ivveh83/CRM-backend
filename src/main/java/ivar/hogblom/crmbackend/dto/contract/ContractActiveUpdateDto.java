package ivar.hogblom.crmbackend.dto.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO used to update the active status of a contract")
public record ContractActiveUpdateDto(

        @Schema(
                description = "Indicates whether the contract is active",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Boolean active,

        @Schema(
                description = "Details or reason for the status change",
                example = "The contract has been extended",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty
        String detail
) {}
