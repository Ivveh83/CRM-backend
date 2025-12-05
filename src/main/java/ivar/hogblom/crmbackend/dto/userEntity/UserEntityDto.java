package ivar.hogblom.crmbackend.dto.userEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserEntityDto(
        UUID id,

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 100, message = "Name must be between 4 and 100 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must be less than 150 characters")
        String email,

        List<String> roles
) {}

