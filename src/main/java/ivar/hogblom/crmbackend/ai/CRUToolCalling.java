package ivar.hogblom.crmbackend.ai;

import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.crm.service.customer.CustomerService;
import ivar.hogblom.crmbackend.crm.service.reseller.ResellerService;
import ivar.hogblom.crmbackend.crm.service.subscription.SubscriptionService;
import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import ivar.hogblom.crmbackend.dto.contract.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerResponseDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionRequestDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionResponseDto;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CRUToolCalling {

    private final ContractService contractService;
    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final ResellerService resellerService;

    public CRUToolCalling(
            @Qualifier("validatingContractService")
            ContractService contractService,
            @Qualifier("validatingCustomerService")
            CustomerService customerService,
            SubscriptionService subscriptionService,
            @Qualifier("validatingResellerService")
            ResellerService resellerService) {
        this.contractService = contractService;
        this.customerService = customerService;
        this.subscriptionService = subscriptionService;
        this.resellerService = resellerService;
    }

    // -------------------- CONTRACT --------------------

    @Tool(description = "Create a new contract.")
    public void createContract(ContractRequestDto request) {
        contractService.createContract(request);
    }

    @Tool(description = "Update an existing contract.")
    public void updateContract(UUID id, ContractRequestDto request) {
        contractService.updateContract(id, request);
    }

    @Tool(description = "Activate or deactivate a contract.")
    public void updateContractActive(UUID id, ContractActiveUpdateDto dto) {
        contractService.updateContractActive(id, dto);
    }

    @Tool(description = "Renew a contract.")
    public void renewContract(UUID id, ContractRenewalDto dto) {
        contractService.renewContract(id, dto);
    }

    @Tool(description = "Retrieve a contract by id.")
    public ContractResponseDto retrieveContractById(UUID id) {
        return contractService.findById(id);
    }

    // -------------------- CUSTOMER --------------------

    @Tool(description = "Create a new customer.")
    public void createCustomer(CustomerRequestDto customerRequestDto) {
        customerService.createCustomer(customerRequestDto);
    }

    @Tool(description = "Update a customer.")
    public void updateCustomer(UUID id, CustomerRequestDto customerRequestDto) {
        customerService.updateCustomer(id, customerRequestDto);
    }

    @Tool(description = "Retrieve a single customer by id.")
    public CustomerResponseDto findCustomerById(UUID id) {
        return customerService.findById(id);
    }

    @Tool(description = "Retrieve all customers.")
    public List<CustomerForContractComponentsDto> retrieveAllCustomer() {
        return customerService.findAllCustomersForContractComponents();
    }

    // -------------------- RESELLER --------------------

    @Tool(description = "Create a new reseller.")
    public void createReseller(ResellerRequestDto request) {
        resellerService.createReseller(request);
    }

    @Tool(description = "Update a reseller.")
    public void updateReseller(UUID id, ResellerRequestDto request) {
        resellerService.updateReseller(id, request);
    }

    @Tool(description = "Activate or deactivate a reseller.")
    public void updateResellerActive(UUID id, boolean active) {
        resellerService.updateResellerActive(id, active);
    }

    @Tool(description = "Retrieve a single reseller by id.")
    public ResellerResponseDto retrieveResellerById(UUID id) {
        return resellerService.findById(id);
    }

    @Tool(description = "Retrieve all resellers.")
    public List<ResellerForContractComponentsDto> retrieveAllResellers() {
        return resellerService.findAllResellersForContractComponents();
    }

    // -------------------- SUBSCRIPTION --------------------

    @Tool(description = "Create a new subscription.")
    public void createSubscription(SubscriptionRequestDto request) {
        subscriptionService.createSubscription(request);
    }

    @Tool(description = "Update a subscription.")
    public void updateSubscription(UUID id, SubscriptionRequestDto request) {
        subscriptionService.updateSubscription(id, request);
    }

    @Tool(description = "Activate or deactivate a subscription.")
    public void updateSubscriptionActive(UUID id, boolean active) {
        subscriptionService.updateSubscriptionActive(id, active);
    }

    @Tool(description = "Retrieve a subscription by id.")
    public SubscriptionResponseDto retrieveSubscriptionById(UUID id) {
        return subscriptionService.findById(id);
    }

    @Tool(description = "Retrieve all subscriptions.")
    public List<SubscriptionForContractComponentsDto> retrieveAllSubscriptions() {
        return subscriptionService.findAllSubscriptionsForContractComponents();
    }
}
