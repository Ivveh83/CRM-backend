package ivar.hogblom.crmbackend.controller;

import ivar.hogblom.crmbackend.dto.SubscriptionEventDto;
import ivar.hogblom.crmbackend.service.SubscriptionEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionEventController {

    private final SubscriptionEventService subscriptionEventService;

    // -----------------------------------------------------
    // 🔵 GET EVENT HISTORY FOR A SUBSCRIPTION
    // -----------------------------------------------------
    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get event history for a subscription",
            description = "Fetches all subscription events sorted by timestamp (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubscriptionEventDto> getSubscriptionEvents(@PathVariable UUID id) {
        return subscriptionEventService.getEventsForSubscription(id);
    }
}
