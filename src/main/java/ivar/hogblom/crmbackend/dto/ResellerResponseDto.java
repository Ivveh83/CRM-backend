package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ResellerResponseDto(
        UUID id,
        String name,
        boolean active,
        String orgNo,
        String address,
        String contactEmail,
        String contactTelephone,
        String invoiceReference,
        LocalDate createdAt
) {}
