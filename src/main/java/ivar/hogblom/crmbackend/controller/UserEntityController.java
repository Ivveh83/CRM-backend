package ivar.hogblom.crmbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.ChangePasswordRequestDto;
import ivar.hogblom.crmbackend.dto.UserEntityDto;
import ivar.hogblom.crmbackend.dto.UserEntityRegistrationDto;
import ivar.hogblom.crmbackend.service.UserEntityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
@Tag(name = "User API", description = "API endpoints for managing Users")
@SecurityRequirement(name = "bearerAuth")
public class UserEntityController {

    private final UserEntityService userEntityService;

    @Autowired
    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    // -----------------------------------------------------
    // 🔵 REGISTER USER
    // -----------------------------------------------------
    @Operation(
            summary = "Register a new user",
            description = "Creates and stores a new user in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntityDto register(
            @Valid
            @RequestBody
            @NotNull(message = "UserEntity cannot be null")
            UserEntityRegistrationDto userEntityRegistrationDto
    ) {
        return userEntityService.create(userEntityRegistrationDto);
    }

    // -----------------------------------------------------
    // 🔐 CHANGE PASSWORD
    // -----------------------------------------------------
    @Operation(
            summary = "Change user password",
            description = """
                    Allows an authenticated user to change their password.
                    Admins can change any user's password.
                    Users can only change their own password.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot edit another user's password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN') or #username.toString() == authentication.principal.username")
    @PatchMapping("/{username}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordRequestDto dto
    ) {
        userEntityService.changePassword(username, dto);
        return ResponseEntity.ok("Password updated successfully");
    }
}
