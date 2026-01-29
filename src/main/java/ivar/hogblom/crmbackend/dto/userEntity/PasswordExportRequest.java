package ivar.hogblom.crmbackend.dto.userEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Password export request")
@Builder
public record PasswordExportRequest(
        @Schema(
                description = "Plain text password to be exported",
                example = "mySecretPassword"
        )
        String password
) {}
