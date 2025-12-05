package ivar.hogblom.crmbackend.controller.subscription;

import ivar.hogblom.crmbackend.dto.subscription.SubscriptionEventDto;
import ivar.hogblom.crmbackend.service.subscription.SubscriptionEventService;
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

    // -----------------------------------------------------
    // 🔴 DELETE SINGLE EVENT
    // -----------------------------------------------------
    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Delete a single subscription event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable UUID eventId) {
        subscriptionEventService.deleteEvent(eventId);
    }

    // -----------------------------------------------------
    // 🔴 DELETE ALL EVENTS FOR A SUBSCRIPTION
    // -----------------------------------------------------
    @DeleteMapping("/{id}/events")
    @Operation(summary = "Delete ALL events for a subscription")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All events deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllEventsForSubscription(@PathVariable UUID id) {
        subscriptionEventService.deleteAllEventsForSubscription(id);
    }
}
