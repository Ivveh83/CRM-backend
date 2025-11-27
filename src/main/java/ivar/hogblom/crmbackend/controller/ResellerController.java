package ivar.hogblom.crmbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.*;
import ivar.hogblom.crmbackend.service.ResellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reseller")
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Reseller API", description = "API endpoints for managing resellers")
public class ResellerController {

    private final ResellerService resellerService;

    @Autowired
    public ResellerController(ResellerService resellerService) {
        this.resellerService = resellerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all resellers",
            description = "Retrieves a list of all resellers for use in reseller list view")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reseller list")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResellerResponseDto> getAllResellers() {
        return resellerService.findAllResellers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all resellers",
            description = "Retrieves a list of all resellers for use in contract components (dropdowns)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reseller list")
    @GetMapping("/getAllResellersForContractComponents")
    @ResponseStatus(HttpStatus.OK)
    public List<ResellerForContractComponentsDto> getAllResellersForContractComponents() {
        return resellerService.findAllResellersForContractComponents();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get reseller by ID",
            description = "Retrieves a single reseller with full details"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reseller")
    @ApiResponse(responseCode = "404", description = "Reseller not found")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResellerResponseDto getResellerById(@PathVariable UUID id) {
        return resellerService.findById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new reseller")
    @PreAuthorize("hasRole('ADMIN')")
    public void createReseller(@RequestBody @Valid ResellerRequestDto request) {
        resellerService.createReseller(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update existing reseller",
            description = "Updates an existing reseller by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseller updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Reseller not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateReseller(
            @PathVariable UUID id,
            @RequestBody @Valid ResellerRequestDto request
    ) {
        resellerService.updateReseller(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete reseller",
            description = "Deletes an existing reseller by its ID, also deletes it from every Contract"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseller deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Reseller not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReseller(@PathVariable UUID id) {
        resellerService.deleteReseller(id);
    }

    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update reseller active status",
            description = "Activate or deactivate a reseller"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseller status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Reseller not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateResellerActive(
            @PathVariable UUID id,
            @RequestBody @Valid ResellerActiveUpdateDto request
    ) {
        resellerService.updateResellerActive(id, request.active());
    }


}
