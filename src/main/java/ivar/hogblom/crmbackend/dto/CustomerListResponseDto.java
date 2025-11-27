package ivar.hogblom.crmbackend.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerListResponseDto(
        UUID id,
        String companyName,
        String orgNo,
        String contactName,
        String country,
        String industry,
        String customerType,
        LocalDate createdAt,
        String notes
) {}
