package ivar.hogblom.crmbackend.controller.userEntityAndRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import ivar.hogblom.crmbackend.dto.role.RoleRequestDto;
import ivar.hogblom.crmbackend.dto.role.RoleResponseDto;
import ivar.hogblom.crmbackend.service.role.RoleService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/role")
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Role API", description = "API endpoints for managing roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // -----------------------------------------------------
    // 🔵 GET ALL ROLES (ADMIN)
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all roles in the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved roles")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoleResponseDto> getAllRoles() {
        return roleService.getAllRoles();
    }

    // -----------------------------------------------------
    // 🟢 CREATE NEW ROLE (ADMIN)
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new role",
            description = "Adds a new role to the system (example: ROLE_MANAGER)"
    )
    @ApiResponse(responseCode = "201", description = "Role successfully created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRole(
            @Valid
            @RequestBody RoleRequestDto dto
    ) {
        roleService.createRole(dto);
    }

    // -----------------------------------------------------
    // 🔴 DELETE ROLE
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a role by ID")
    public void deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
    }
}
