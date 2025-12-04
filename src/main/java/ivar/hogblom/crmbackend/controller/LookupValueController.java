package ivar.hogblom.crmbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import ivar.hogblom.crmbackend.dto.*;
import ivar.hogblom.crmbackend.service.LookupValueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Lookup API", description = "API endpoints for managing dynamic dropdown values")
public class LookupValueController {

    private final LookupValueService lookupService;

    // -----------------------------------------------------
    // 🔵 CREATE NEW LOOKUP VALUE
    // -----------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new lookup value")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lookup value created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void createLookupValue(
            @RequestBody @Valid LookupValueCreateDto request
    ) {
        lookupService.create(request);
    }

// -----------------------------------------------------
// 🔵 UPDATE LOOKUP VALUE
// -----------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update lookup value",
            description = "Updates label, value and sort order for a lookup entry"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lookup value updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Lookup value not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateLookupValue(
            @PathVariable String id,
            @RequestBody @Valid LookupValueUpdateDto request
    ) {
        lookupService.update(id, request);
    }


    // -----------------------------------------------------
// 🔵 GET ALL LOOKUP VALUES (ACTIVE + INACTIVE)
// -----------------------------------------------------
    @GetMapping("/{type}/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all lookup values (active + inactive)",
            description = "Retrieves every lookup value for a given type, without filtering on active state. " +
                    "Useful for admin panels where values can be sorted or toggled."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved lookup values")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<LookupValueResponseDto> getAllLookupValues(
            @PathVariable String type
    ) {
        return lookupService.getAllByTypeAndActive(type, false); // false = don't filter activeOnly
    }


    // -----------------------------------------------------
    // 🔵 GET LOOKUP VALUES BY TYPE (ONLY ACTIVE)
    // -----------------------------------------------------
    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get lookup values by type",
            description = "Retrieves all values for a given lookup type. Can filter only active entries.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved lookup values")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<LookupValueResponseDto> getLookupValues(
            @PathVariable String type,
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        return lookupService.getAllByTypeAndActive(type, activeOnly);
    }

    // -----------------------------------------------------
    // 🔵 UPDATE ACTIVE ONLY
    // -----------------------------------------------------
    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Toggle lookup active state")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Active state updated"),
            @ApiResponse(responseCode = "404", description = "Lookup value not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateLookupActive(
            @PathVariable String id,
            @RequestBody @Valid LookupValueUpdateActiveDto request
    ) {
        lookupService.updateActive(id, request.active());
    }

    // -----------------------------------------------------
// 🔵 REORDER LOOKUP VALUES (drag & drop sorting)
// -----------------------------------------------------
    @PostMapping("/{type}/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reorder lookup values",
            description = "Updates the sortOrder for all lookup values of a given type based on drag-and-drop ordering.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lookup values reordered"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "One or more lookup values were not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void reorderLookupValues(
            @PathVariable String type,
            @RequestBody @Valid List<LookupSortUpdateDto> updates
    ) {
        lookupService.updateSortOrder(type, updates);
    }


}
