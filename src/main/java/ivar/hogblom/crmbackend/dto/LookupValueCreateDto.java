package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

@Builder
public record LookupValueCreateDto(
        String type,
        String label,
        Integer sortOrder
) {}
