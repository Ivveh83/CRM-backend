package ivar.hogblom.crmbackend.dto;

import jakarta.validation.constraints.NotNull;

public record ContractActiveUpdateDto(
        @NotNull Boolean active
) {}
