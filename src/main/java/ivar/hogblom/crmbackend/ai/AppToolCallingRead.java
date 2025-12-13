package ivar.hogblom.crmbackend.ai;

import ivar.hogblom.crmbackend.crm.service.contract.ContractEventService;
import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.crm.service.customer.CustomerService;
import ivar.hogblom.crmbackend.dto.contract.ContractEventDto;
import ivar.hogblom.crmbackend.dto.contract.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AppToolCallingRead {

    private final ContractEventService contractEventService;
    private final ContractService contractService;
    private final CustomerService customerService;

    @Autowired
    public AppToolCallingRead(ContractEventService contractEventService, @Qualifier("mainContractService")ContractService contractService, CustomerService customerService) {
        this.contractEventService = contractEventService;
        this.contractService = contractService;
        this.customerService = customerService;
    }

    @Tool(description = "Search for all events concerning a specific contract")
    public String getContractEvents(UUID contractId) {
        try {
            List<ContractEventDto> list = contractEventService.getEventsForContract(contractId);
            return "Successfully found these events: " + list;
        } catch (Exception ex) {
            return "Error by searching due to " +  ex.getMessage();
        }
    }

    @Tool(description = "Search for all contracts")
    public String getAllContracts() {
        try {
            List<ContractResponseDto> list = contractService.findAll();
            return "Successfully found these contracts: " + list;
        } catch (Exception ex) {
            return "Error by searching due to " +  ex.getMessage();
        }
    }

    @Tool(description = "Search for all customers")
    public String getAllCustomers() {
        try {
            List<CustomerResponseDto> list = customerService.findAll();
            return "Successfully found these customers: " + list;
        } catch (Exception ex) {
            return "Error by searching due to " +  ex.getMessage();
        }
    }
}
