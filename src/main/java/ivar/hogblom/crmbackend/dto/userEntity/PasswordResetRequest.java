package ivar.hogblom.crmbackend.dto.userEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Password export request")
@Builder
public record PasswordResetRequest(
        @Schema(
                description = "Username for whom password is to be exported",
                example = "adam123"
        )
        String username
) {}
