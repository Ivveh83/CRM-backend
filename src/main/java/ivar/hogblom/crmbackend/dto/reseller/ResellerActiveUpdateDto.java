package ivar.hogblom.crmbackend.dto.reseller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "ResellerActiveUpdate",
        description = "Request payload for activating or deactivating a reseller."
)
public record ResellerActiveUpdateDto(

        @Schema(
                description = "Set to true to activate the reseller, false to deactivate it.",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Boolean active

) {}
