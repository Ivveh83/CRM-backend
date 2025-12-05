package ivar.hogblom.crmbackend.dto.lookup;

import lombok.Builder;

@Builder
public record LookupValueResponseDto(
        String id,
        String type,
        String value,
        String label,
        Integer sortOrder,
        boolean active
) {}
