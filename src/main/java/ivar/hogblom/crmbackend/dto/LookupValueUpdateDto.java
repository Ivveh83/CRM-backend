package ivar.hogblom.crmbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload for updating a lookup value")
public record LookupValueUpdateDto(

        @Schema(description = "Human-readable label shown in UI")
        @NotBlank(message = "Label may not be empty")
        String label,

        @Schema(description = "Sort order used for UI positioning", example = "1")
        @NotNull(message = "Sort order is required")
        Integer sortOrder
) {}
