package ivar.hogblom.crmbackend.dto.userEntity;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AddRoleToUserDto(
        UUID userId,
        String roleName
) {
}
