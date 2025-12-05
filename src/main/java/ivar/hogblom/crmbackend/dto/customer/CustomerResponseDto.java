package ivar.hogblom.crmbackend.dto.customer;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CustomerResponseDto(
        UUID id,
        String companyName,
        String orgNo,
        String contactName,
        String contactEmail,
        String contactPhone,
        String address,
        String city,
        String zipCode,
        String country,
        String industry,
        String customerType,
        LocalDate createdAt,
        String notes
) {}
