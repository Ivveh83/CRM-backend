package ivar.hogblom.crmbackend.controller.contract;

import ivar.hogblom.crmbackend.dto.contract.ContractEventDto;
import ivar.hogblom.crmbackend.service.contract.ContractEventService;
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
@RequestMapping("/api/contract")
public class ContractEventController {

    private final ContractEventService contractEventService;

    // -----------------------------------------------------
    // 🔵 GET EVENT HISTORY FOR A CONTRACT
    // -----------------------------------------------------
    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get event history for a contract",
            description = "Fetches all contract events sorted by timestamp (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<ContractEventDto> getContractEvents(@PathVariable UUID id) {

        return contractEventService.getEventsForContract(id);
    }
    // -----------------------------------------------------
    // 🔴 DELETE SINGLE EVENT
    // -----------------------------------------------------
    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Delete a single contract event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable UUID eventId) {
        contractEventService.deleteEvent(eventId);
    }
    // -----------------------------------------------------
    // 🔴 DELETE ALL EVENTS FOR A CONTRACT
    // -----------------------------------------------------
    @DeleteMapping("/{id}/events")
    @Operation(summary = "Delete ALL events for a contract")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All events deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllEvents(@PathVariable UUID id) {
        contractEventService.deleteAllEventsForContract(id);
    }
}
