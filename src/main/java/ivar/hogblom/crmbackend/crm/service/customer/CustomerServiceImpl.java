package ivar.hogblom.crmbackend.crm.service.customer;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;
import ivar.hogblom.crmbackend.crm.repository.customer.CustomerRepository;
import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.util.CustomerCloneUtil;
import ivar.hogblom.crmbackend.util.CustomerDiffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequireCrmDatabase
@Service
@Transactional(transactionManager = "crmTransactionManager")
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerCloneUtil customerCloneUtil;
    private final CustomerDiffUtil customerDiffUtil;
    private final ContractService contractService;
    private final CustomerEventService customerEventService;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerCloneUtil customerCloneUtil, CustomerDiffUtil customerDiffUtil, @Qualifier("mainContractService")ContractService contractService, CustomerEventService customerEventService) {
        this.customerRepository = customerRepository;
        this.customerCloneUtil = customerCloneUtil;
        this.customerDiffUtil = customerDiffUtil;
        this.contractService = contractService;
        this.customerEventService = customerEventService;
    }

    @Override
    public List<CustomerListResponseDto> findAllCustomersForCustomerListComponent() {
        return customerRepository.findAll()
                .stream()
                .map(this::toCustomerListResponseDto)
                .toList();
    }

    @Override
    public List<CustomerForContractComponentsDto> findAllCustomersForContractComponents() {
        return customerRepository.findAll().stream()
                .map(this::toCustomerForContractComponentsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponseDto> findAll() {
        return customerRepository.findAll().stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    public CustomerResponseDto findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        return toResponseDto(customer);
    }

    @Override
    public void createCustomer(CustomerRequestDto request) {

        // 🛑 Validation – unique fields
        if (customerRepository.existsByCompanyName(request.companyName())) {
            throw new IllegalArgumentException("Company name already exists");
        }
        if (request.orgNo() != null &&
                customerRepository.existsByOrgNo(request.orgNo())) {
            throw new IllegalArgumentException("Org no already exists");
        }
        Customer customer = toEntity(request);
        Customer savedC = customerRepository.save(customer);
        customerEventService.logCustomerCreated(savedC);
    }

    @Override
    
    public void updateCustomer(UUID id, CustomerRequestDto request) {

        // 1. Hämta befintlig kund
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        // 2. Duplikatkontroll innan save()

        boolean orgNoExists = customerRepository.existsByOrgNoAndIdNot(request.orgNo(), id);
        if (orgNoExists) {
            throw new RuntimeException("Org no already exists");
        }

        boolean companyNameExists = customerRepository.existsByCompanyNameAndIdNot(request.companyName(), id);
        if (companyNameExists) {
            throw new RuntimeException("Company name already exists");
        }

        // 3. Klona BEFORE
        Customer oldCopy = customerCloneUtil.clone(existing);

        // 4. Mappa DTO → entity
        Customer toUpdate = toEntity(request);
        toUpdate.setId(existing.getId());

        // 5. Spara AFTER
        Customer updated = customerRepository.save(toUpdate);

        // 6. Skapa diffar
        List<String> customerDiffs = customerDiffUtil.diff(oldCopy, updated);

        // 7. Event + loggning
        contractService.handleCustomerUpdated(oldCopy, updated, customerDiffs);
        customerEventService.logCustomerUpdated(updated, customerDiffs);
    }



    
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        if (!customer.getContracts().isEmpty()) {
            throw new RuntimeException("Customer cannot be deleted because it is used in contracts");
        }

        //Transactional (Rollback if something goes wrong)
        customerEventService.logCustomerDeleted(customer);

        //Transactional
        customerRepository.delete(customer);
    }


    private CustomerListResponseDto toCustomerListResponseDto(Customer c) {
        return new CustomerListResponseDto(
                c.getId(),
                c.getCompanyName(),
                c.getOrgNo(),
                c.getContactName(),
                c.getCountry(),
                c.getIndustry(),
                c.getCustomerType(),
                c.getCreatedAt(),
                c.getNotes()
        );
    }

    private CustomerForContractComponentsDto toCustomerForContractComponentsDto(Customer c) {

        if (c == null) {
            return null;
        }

        return CustomerForContractComponentsDto.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .orgNo(c.getOrgNo())
                .build();
    }

    private Customer toEntity(CustomerRequestDto dto) {
        return Customer.builder()
                .companyName(dto.companyName())
                .orgNo(dto.orgNo())
                .contactName(dto.contactName())
                .contactEmail(dto.contactEmail())
                .contactPhone(dto.contactPhone())
                .address(dto.address())
                .city(dto.city())
                .zipCode(dto.zipCode())
                .country(dto.country())
                .industry(dto.industry())
                .customerType(dto.customerType())
                .createdAt(dto.createdAt())
                .notes(dto.notes())
                .build();
    }

    private CustomerResponseDto toResponseDto(Customer customer) {
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .companyName(customer.getCompanyName())
                .orgNo(customer.getOrgNo())
                .contactName(customer.getContactName())
                .contactEmail(customer.getContactEmail())
                .contactPhone(customer.getContactPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .zipCode(customer.getZipCode())
                .country(customer.getCountry())
                .industry(customer.getIndustry())
                .customerType(customer.getCustomerType())
                .createdAt(customer.getCreatedAt())
                .notes(customer.getNotes())
                .build();
    }
}
