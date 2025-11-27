package ivar.hogblom.crmbackend.dto;

import jakarta.validation.constraints.NotNull;

public record ResellerActiveUpdateDto(
        @NotNull Boolean active
) {}
