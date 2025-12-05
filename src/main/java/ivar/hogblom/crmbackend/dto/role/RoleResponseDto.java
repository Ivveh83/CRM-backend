package ivar.hogblom.crmbackend.dto.role;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RoleResponseDto(
        UUID id,
        String name
) {
}
