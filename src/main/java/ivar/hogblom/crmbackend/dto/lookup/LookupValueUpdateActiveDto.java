package ivar.hogblom.crmbackend.dto.lookup;

import lombok.Builder;

@Builder
public record LookupValueUpdateActiveDto(
        boolean active
) {}
