package ivar.hogblom.crmbackend.dto.contract;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record ContractRequestDto(
                                 @NotEmpty List<@NotNull UUID> subscriptionIds,
                                 @NotEmpty List<@NotNull UUID> resellerIds,
                                 @NotNull UUID customerId,
                                 @NotNull LocalDate contractDate,
                                 @NotNull LocalDate dueDate,
                                 List<LocalDate> renewalDates,
                                 @NotNull Double totalPricePerMonth,
                                 boolean status,
                                 boolean active,
                                 @NotNull Integer contractLengthMonths,
                                 String comment
) {}
