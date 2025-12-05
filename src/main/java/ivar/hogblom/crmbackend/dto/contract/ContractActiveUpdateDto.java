package ivar.hogblom.crmbackend.dto.contract;

import jakarta.validation.constraints.NotNull;

public record ContractActiveUpdateDto(
        @NotNull Boolean active,
        @NotNull String detail
) {}
