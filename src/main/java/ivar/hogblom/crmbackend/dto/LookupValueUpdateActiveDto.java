package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

@Builder
public record LookupValueUpdateActiveDto(
        boolean active
) {}
