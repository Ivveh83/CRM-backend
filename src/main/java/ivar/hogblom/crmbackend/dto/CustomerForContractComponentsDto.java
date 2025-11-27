package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerForContractComponentsDto(
        UUID id,
        String companyName,
        String orgNo

) {
}
