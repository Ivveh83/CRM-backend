package ivar.hogblom.crmbackend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.ContractRequestDto;
import ivar.hogblom.crmbackend.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contract")
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Contract API", description = "API endpoints for managing contracts")
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    // -----------------------------------------------------
    // 🔵 GET ALL CONTRACTS
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all contracts", description = "Retrieves a list of all contracts items, " +
            "with names for customer, resellers and subscriptions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved contract list")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContractResponseDto> getAllContracts() {
        return contractService.findAll();
    }

    // -----------------------------------------------------
    // 🔵 GET CONTRACT BY ID
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get contract by ID",
            description = "Retrieves a single contract with names for customer, resellers and subscriptions"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved contract")
    @ApiResponse(responseCode = "404", description = "Contract not found")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContractResponseDto getContractById(@PathVariable UUID id) {
        return contractService.findById(id);
    }

    // -----------------------------------------------------
    // 🔵 CREATE NEW CONTRACT
    // -----------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new contract")
    @PreAuthorize("hasRole('ADMIN')")
    public void createContract(@RequestBody @Valid ContractRequestDto request) {
        contractService.createContract(request);
    }

    // -----------------------------------------------------
    // 🔵 UPDATE EXISTING CONTRACT
    // -----------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update existing contract",
            description = "Updates an existing contract by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contract updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateContract(
            @PathVariable UUID id,
            @RequestBody @Valid ContractRequestDto request
    ) {
        contractService.updateContract(id, request);
    }

    // -----------------------------------------------------
    // 🔵 DELETE CONTRACT
    // -----------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete contract",
            description = "Deletes an existing contract by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contract deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteContract(@PathVariable UUID id) {
        contractService.deleteContract(id);
    }

    // -----------------------------------------------------
    // 🔵 UPDATE ACTIVE STATE ONLY
    // -----------------------------------------------------
    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Toggle contract active state",
            description = "Updates only the 'active' state of a contract"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contract updated successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateContractActive(
            @PathVariable UUID id,
            @RequestBody @Valid ContractActiveUpdateDto request
    ) {
        contractService.updateContractActive(id, request.active());
    }
}

