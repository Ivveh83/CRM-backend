package ivar.hogblom.crmbackend.dto.lookup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LookupSortUpdateDto(

        @Schema(description = "Lookup value ID", example = "f32b8c3b-1424-489e-91df-cb96e9530b44")
        @NotBlank(message = "ID may not be blank")
        String id,

        @Schema(description = "New sort order (must be >= 1)", example = "1")
        @Min(value = 1, message = "Sort order must be at least 1")
        Integer sortOrder
) {}
