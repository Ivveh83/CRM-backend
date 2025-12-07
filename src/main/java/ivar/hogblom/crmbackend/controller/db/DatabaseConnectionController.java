package ivar.hogblom.crmbackend.controller.db;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.system.service.db.DatabaseConnectionService;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/db")
@Tag(name = "Database Connection", description = "Database connection management APIs")
public class DatabaseConnectionController {

    private final DatabaseConnectionService databaseConnectionService;

    public DatabaseConnectionController(
            @Qualifier("databaseConnectionSecured")DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    // -----------------------------------------------------
    // 🔵 CONNECT TO DATABASE
    // -----------------------------------------------------
    @Operation(
            summary = "Connect to a database",
            description = "Connects the authenticated user to a specific database by ID"
    )
    @ApiResponse(responseCode = "200", description = "Successfully connected to database")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Database not found")
    @PostMapping("/connect/{id}")
    public DatasourceResponseDto connectToDatabase(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            @RequestHeader("Authorization") String authHeader
    ) {
        return databaseConnectionService.connectToDatabase(id, principal, authHeader);
    }
}
