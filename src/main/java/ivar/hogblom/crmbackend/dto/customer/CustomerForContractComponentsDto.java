package ivar.hogblom.crmbackend.dto.customer;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerForContractComponentsDto(
        UUID id,
        String companyName,
        String orgNo

) {
}
