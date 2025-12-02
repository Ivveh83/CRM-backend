package ivar.hogblom.crmbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerRequestDto(
        @NotNull String companyName,
        @NotNull String orgNo,
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
