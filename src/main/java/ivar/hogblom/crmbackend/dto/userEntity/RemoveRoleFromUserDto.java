package ivar.hogblom.crmbackend.dto.userEntity;

import java.util.UUID;

public record RemoveRoleFromUserDto(
        UUID userId,
        String roleName
) {}
