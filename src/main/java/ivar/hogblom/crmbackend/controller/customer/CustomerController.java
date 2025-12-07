package ivar.hogblom.crmbackend.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import ivar.hogblom.crmbackend.crm.service.customer.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Customer API", description = "API endpoints for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // -----------------------------------------------------
    // 🔵 GET ALL CUSTOMERS (LIST VIEW)
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all customers",
            description = "Retrieves a list of all customers"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerListResponseDto> getAllCustomers() {
        return customerService.findAllCustomersForCustomerListComponent();
    }

    // -----------------------------------------------------
    // 🔵 GET CUSTOMER BY ID
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves a single customer with full details"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponseDto getCustomerById(@PathVariable UUID id) {
        return customerService.findById(id);
    }

    // -----------------------------------------------------
    // 🔵 GET ALL CUSTOMERS FOR CONTRACT COMPONENTS
    // -----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all customers for contracts components",
            description = "Retrieves a list of all customers for use in contract components (dropdowns)"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list")
    @GetMapping("/getAllCustomersForContractComponents")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerForContractComponentsDto> getAllCustomersForContractComponents() {
        return customerService.findAllCustomersForContractComponents();
    }

    // -----------------------------------------------------
    // 🔵 CREATE NEW CUSTOMER
    // -----------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new customer")
    @PreAuthorize("hasRole('ADMIN')")
    public void createCustomer(@RequestBody @Valid CustomerRequestDto request) {
        customerService.createCustomer(request);
    }

    // -----------------------------------------------------
    // 🔵 UPDATE EXISTING CUSTOMER
    // -----------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Update existing customer",
            description = "Updates an existing customer by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCustomer(
            @PathVariable UUID id,
            @RequestBody @Valid CustomerRequestDto request
    ) {
        customerService.updateCustomer(id, request);
    }

    // -----------------------------------------------------
    // 🔵 DELETE CUSTOMER
    // -----------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete customer",
            description = "Deletes an existing customer by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
    }

}

