package ivar.hogblom.crmbackend.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record ContractResponseDto(
        UUID id,
        CustomerForContractComponentsDto customer,
        List<ResellerForContractComponentsDto> resellers,
        List<SubscriptionForContractComponentsDto> subscriptionTypes,
        boolean status,
        boolean active,
        LocalDate contractDate,
        Integer contractLengthMonths,
        List<LocalDate> renewalDates,
        Double totalPricePerMonth,
        LocalDate dueDate,
        String comment
) {}
