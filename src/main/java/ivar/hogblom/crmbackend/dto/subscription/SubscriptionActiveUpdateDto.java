package ivar.hogblom.crmbackend.dto.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(
        name = "SubscriptionActiveUpdate",
        description = "Request payload for activating or deactivating a subscription."
)
public record SubscriptionActiveUpdateDto(

        @Schema(
                description = "Set to true to activate the subscription, false to deactivate it.",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Active status must not be null")
        Boolean active

) {}
