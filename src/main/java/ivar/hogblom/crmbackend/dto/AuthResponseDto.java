package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

@Builder
public record AuthResponseDto(
        String token,
        String type,
        String username,
        String email,
        String[] roles
) {}