package ivar.hogblom.crmbackend.dto.db;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DatabaseConnectionResponseDto(
        UUID id,
        String type,
        String databaseName,
        String filePath
) {
}
