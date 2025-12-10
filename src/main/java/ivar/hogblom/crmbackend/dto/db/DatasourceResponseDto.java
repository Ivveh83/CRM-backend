package ivar.hogblom.crmbackend.dto.db;

import lombok.Builder;

@Builder(toBuilder = true)
public record DatasourceResponseDto(
        String token, String dbKey
) {
}
