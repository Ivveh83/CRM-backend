package ivar.hogblom.crmbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Data for renewing a contract")
public record ContractRenewalDto(

        @NonNull
        @Schema(description = "New due date after renewal", example = "2025-06-01")
        LocalDate dueDate,

        @NonNull
        @Schema(description = "List of all renewal dates including the new one",
                example = "[\"2024-01-01\", \"2025-01-01\"]")
        List<LocalDate> renewalDates,

        @NotNull
        @Schema(description = "Whether the contract is considered open or closed after renewal",
                example = "false")
        boolean status
) {}
