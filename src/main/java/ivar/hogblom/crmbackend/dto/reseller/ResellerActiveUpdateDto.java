package ivar.hogblom.crmbackend.dto.reseller;

import jakarta.validation.constraints.NotNull;

public record ResellerActiveUpdateDto(
        @NotNull Boolean active
) {}
