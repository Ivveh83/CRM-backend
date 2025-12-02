package ivar.hogblom.crmbackend.controller;

import ivar.hogblom.crmbackend.dto.ResellerEventDto;
import ivar.hogblom.crmbackend.service.ResellerEventService;
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
@RequestMapping("/api/reseller")
public class ResellerEventController {

    private final ResellerEventService resellerEventService;

    // -----------------------------------------------------
    // 🔵 GET EVENT HISTORY FOR A RESELLER
    // -----------------------------------------------------
    @GetMapping("/{id}/events")
    @Operation(
            summary = "Get event history for a reseller",
            description = "Fetches all reseller events sorted by timestamp (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Reseller not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResellerEventDto> getResellerEvents(@PathVariable UUID id) {
        return resellerEventService.getEventsForReseller(id);
    }
}
