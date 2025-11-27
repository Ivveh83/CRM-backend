package ivar.hogblom.crmbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubscriptionActiveUpdateDto(
        @NotNull(message = "Active status must not be null")
        Boolean active
) {}
