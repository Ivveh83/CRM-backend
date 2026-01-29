package ivar.hogblom.crmbackend.controller.db;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.db.DataSourceConfigDto;
import ivar.hogblom.crmbackend.dto.db.DatabaseConnectionResponseDto;
import ivar.hogblom.crmbackend.dto.db.DisconnectResponseDto;
import ivar.hogblom.crmbackend.system.service.db.DatabaseConnectionService;
import ivar.hogblom.crmbackend.dto.db.DatasourceResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    // 🟢 CREATE DATABASE CONNECTION
    // -----------------------------------------------------
    @Operation(
            summary = "Create database connection",
            description = "Creates a database connection profile for the authenticated user. " +
                    "This does NOT connect to the database."
    )
    @ApiResponse(responseCode = "200", description = "Database connection created")
    @ApiResponse(responseCode = "400", description = "Invalid database configuration")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/connections")
    public DatabaseConnectionResponseDto createConnection(
            @RequestBody DataSourceConfigDto dto,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return databaseConnectionService.create(dto, principal);
    }

    // -----------------------------------------------------
    // 🔵 LIST DATABASE CONNECTIONS
    // -----------------------------------------------------
    @Operation(
            summary = "List database connections",
            description = "Lists all database connection profiles owned by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Connections fetched")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/connections")
    public List<DatabaseConnectionResponseDto> listConnections(
            @AuthenticationPrincipal UserDetails principal
    ) {
        return databaseConnectionService.findAllForUser(principal);
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

// -----------------------------------------------------
// 🔴 DISCONNECT FROM DATABASE
// -----------------------------------------------------
    @Operation(
            summary = "Disconnect from database",
            description = "Removes active database from the session and issues a new JWT without dbKey"
    )
    @ApiResponse(responseCode = "200", description = "Disconnected from database")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/disconnect")
    public DisconnectResponseDto disconnectFromDatabase(
            @AuthenticationPrincipal UserDetails principal,
            @RequestHeader("Authorization") String authHeader
    ) {
        return databaseConnectionService.disconnectFromDatabase(principal, authHeader);
    }

// -----------------------------------------------------
// 🔴 DELETE DATABASE CONNECTION
// -----------------------------------------------------
    @Operation(
            summary = "Delete database connection",
            description = """
            Deletes a database connection profile owned by the authenticated user.
            The database must NOT be currently connected.
            """
    )
    @ApiResponse(responseCode = "200", description = "Database connection deleted")
    @ApiResponse(responseCode = "400", description = "Database is currently connected")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Not allowed to delete this connection")
    @ApiResponse(responseCode = "404", description = "Database connection not found")
    @DeleteMapping("/connections/{id}")
    public void deleteConnection(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            @RequestHeader("Authorization") String authHeader
    ) {
        databaseConnectionService.delete(id, principal, authHeader);
    }

}
