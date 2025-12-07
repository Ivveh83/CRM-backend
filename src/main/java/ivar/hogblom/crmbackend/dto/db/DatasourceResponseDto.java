package ivar.hogblom.crmbackend.dto.db;

import lombok.Builder;

@Builder
public record DatasourceResponseDto(
        String token, String dbKey
) {
}
