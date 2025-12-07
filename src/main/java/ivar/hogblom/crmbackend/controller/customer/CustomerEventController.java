package ivar.hogblom.crmbackend.controller.customer;

import ivar.hogblom.crmbackend.dto.customer.CustomerEventDto;
import ivar.hogblom.crmbackend.crm.service.customer.CustomerEventService;
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
@RequestMapping("/api/customer")
public class CustomerEventController {

    private final CustomerEventService customerEventService;

    // -----------------------------------------------------
    // 🔵 GET EVENT HISTORY FOR A CUSTOMER
    // -----------------------------------------------------
    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get event history for a customer",
            description = "Fetches all customer events sorted by timestamp (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerEventDto> getCustomerEvents(@PathVariable UUID id) {
        return customerEventService.getEventsForCustomer(id);
    }

    // -----------------------------------------------------
// 🔴 DELETE SINGLE EVENT
// -----------------------------------------------------
    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Delete a single customer event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable UUID eventId) {
        customerEventService.deleteEvent(eventId);
    }

    // -----------------------------------------------------
// 🔴 DELETE ALL EVENTS FOR A CUSTOMER
// -----------------------------------------------------
    @DeleteMapping("/{id}/events")
    @Operation(summary = "Delete ALL events for a customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All events deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllEventsForCustomer(@PathVariable UUID id) {
        customerEventService.deleteAllEventsForCustomer(id);
    }
}
