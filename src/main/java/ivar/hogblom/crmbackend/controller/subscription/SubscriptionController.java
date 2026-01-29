package ivar.hogblom.crmbackend.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionRequestDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionResponseDto;
import ivar.hogblom.crmbackend.crm.service.subscription.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription")
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Subscription API", description = "API endpoints for managing subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // ---------------------------------------------------------
    //🔵 GET ALL
    // ---------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all subscriptions",
            description = "Retrieves a list of all subscriptions for use in list view")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription list")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionResponseDto> getAllSubscriptions() {
        return subscriptionService.findAll();
    }

    // ---------------------------------------------------------
    //🔵 GET ALL FOR CONTRACT COMPONENTS
    // ---------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all subscriptions for contract components",
            description = "Retrieves subscription entries simplified for contract dropdowns")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription list for components")
    @GetMapping("/getAllSubscriptionsForContractComponents")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionForContractComponentsDto> getAllSubscriptionsForContractComponents() {
        return subscriptionService.findAllSubscriptionsForContractComponents();
    }

    // ---------------------------------------------------------
    //🔵 GET BY ID
    // ---------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get subscription by ID",
            description = "Retrieves a single subscription with full details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionResponseDto getSubscriptionById(@PathVariable UUID id) {
        return subscriptionService.findById(id);
    }

    // ---------------------------------------------------------
    //🔵 CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new subscription")
    @PreAuthorize("hasRole('ADMIN')")
    public void createSubscription(@RequestBody @Valid SubscriptionRequestDto request) {
        subscriptionService.createSubscription(request);
    }

    // ---------------------------------------------------------
    // 🔵 UPDATE
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update existing subscription",
            description = "Updates an existing subscription by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subscription updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSubscription(
            @PathVariable UUID id,
            @RequestBody @Valid SubscriptionRequestDto request
    ) {
        subscriptionService.updateSubscription(id, request);
    }

    // ---------------------------------------------------------
    // 🔵 DELETE
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete subscription",
            description = "Deletes an existing subscription by its ID, also detaches it from relevant contracts"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubscription(@PathVariable UUID id) {
        subscriptionService.deleteSubscription(id);
    }

    // ---------------------------------------------------------
    // 🔵 PATCH ACTIVE (Activate / Pause)
    // ---------------------------------------------------------
    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update subscription active status",
            description = "Activate or deactivate a subscription"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subscription status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSubscriptionActive(
            @PathVariable UUID id,
            @RequestBody @Valid SubscriptionActiveUpdateDto request
    ) {
        subscriptionService.updateSubscriptionActive(id, request.active());
    }
}
