package ivar.hogblom.crmbackend.dto.reseller;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ResellerRequestDto(
        @NotNull String name,
        @NotNull String orgNo,
        @NotNull boolean active,
        String address,
        String contactEmail,
        String contactTelephone,
        String invoiceReference,
        LocalDate createdAt
) {
}
