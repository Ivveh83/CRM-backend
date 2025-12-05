package ivar.hogblom.crmbackend.dto.reseller;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResellerForContractComponentsDto(
        UUID id,
        String name,
        String orgNo,
        boolean active
) {
}
