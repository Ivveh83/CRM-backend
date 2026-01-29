package ivar.hogblom.crmbackend.dto.db;

import lombok.Builder;

@Builder
public record DisconnectResponseDto(
        String token
) {}
