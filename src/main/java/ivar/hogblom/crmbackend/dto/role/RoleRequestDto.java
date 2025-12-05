package ivar.hogblom.crmbackend.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RoleRequestDto(
        @NotNull String name
) {
}
