package ivar.hogblom.crmbackend.dto.userEntity;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChangePasswordRequestDto(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        String newPassword

) {}
