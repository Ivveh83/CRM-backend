package ivar.hogblom.crmbackend.controller.userEntityAndRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import ivar.hogblom.crmbackend.dto.userEntity.*;
import ivar.hogblom.crmbackend.system.service.userEntity.UserEntityService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Validated
@Tag(
        name = "User API",
        description = "Endpoints for managing users in the CRM system"
)
public class UserEntityController {

    private final UserEntityService userEntityService;

    @Autowired
    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    // -----------------------------------------------------
    // 🔵 REGISTER USER (PUBLIC ENDPOINT)
    // -----------------------------------------------------
    @Operation(
            summary = "Register a new user",
            description = "Creates and stores a new user in the system. No authentication required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied")
    })

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid
            @RequestBody
            @NotNull(message = "UserEntity cannot be null")
            UserEntityRegistrationDto userEntityRegistrationDto
    ) {
        userEntityService.create(userEntityRegistrationDto);
    }

    // -----------------------------------------------------
    // 🔵 GET ALL USERS (ADMIN)
    // -----------------------------------------------------
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users. Admin only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserEntityDto> getAllUsers() {
        return userEntityService.getAllUsers();
    }

    // -----------------------------------------------------
    // 🔵 GET USER BY ID (ADMIN)
    // -----------------------------------------------------
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by its UUID. Admin only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserEntityDto getUser(
            @Parameter(description = "UUID of the user to fetch")
            @PathVariable UUID id
    ) {
        return userEntityService.getUser(id);
    }

    // -----------------------------------------------------
    // 🟡 UPDATE USER (ADMIN)
    // -----------------------------------------------------
    @Operation(
            summary = "Update user",
            description = "Updates the username or email of a user. Admin only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public void updateUser(@Valid @RequestBody UserEntityDto userDto) {
        userEntityService.updateUser(userDto);
    }

    // -----------------------------------------------------
    // 🟣 ADD ROLE TO USER (ADMIN)
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addRoleToUser")
    @Operation(summary = "Add role to user", description = "Assigns a role to a specific user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<String> addRoleToUser(@Valid @RequestBody AddRoleToUserDto dto) {
        userEntityService.addRoleToUser(dto);
        return ResponseEntity.ok("Role added to user");
    }


    // -----------------------------------------------------
    // 🔐 CHANGE PASSWORD (ADMIN or SELF)
    // -----------------------------------------------------
    @Operation(
            summary = "Change user password",
            description = """
                Allows a user or admin to change a password.
                Admins may change any user's password.
                Users may only change their own password.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })

    @PreAuthorize("hasRole('ADMIN') or #dto.username == authentication.principal.username")
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto
    ) {
        userEntityService.changePassword(dto);
        return ResponseEntity.ok("Password updated successfully");
    }

    // -----------------------------------------------------
    // 🔴 REMOVE ROLE FROM USER (ADMIN)
    // -----------------------------------------------------

    @Operation(
            summary = "Remove role from user",
            description = "Removes a role from a specific user. Admin only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role removed from user"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden – only admins can remove roles")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/role")
    public ResponseEntity<String> removeRoleFromUser(
            @Valid @RequestBody RemoveRoleFromUserDto dto
    ) {
        userEntityService.removeRoleFromUser(dto);
        return ResponseEntity.ok("Role removed from user");
    }


    // -----------------------------------------------------
    // 🔴 DELETE USER (ADMIN)
    // -----------------------------------------------------
    @Operation(
            summary = "Delete a user",
            description = "Removes a user from the system. Admin only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "UUID of the user to delete")
            @PathVariable UUID id
    ) {
        userEntityService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
