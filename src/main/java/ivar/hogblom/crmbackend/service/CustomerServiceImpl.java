package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.CustomerResponseDto;
import ivar.hogblom.crmbackend.entity.Customer;
import ivar.hogblom.crmbackend.repository.CustomerRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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

    public CustomerResponseDto findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        return toResponseDto(customer);
    }


    @Override
    @Transactional
    public void updateCustomer(UUID id, CustomerRequestDto request) {

        customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        Customer updatedCustomer = toEntity(request);
        updatedCustomer.setId(id);
        customerRepository.save(updatedCustomer);
    }

    @Transactional
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
        customerRepository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        if (!customer.getContracts().isEmpty()) {
            throw new RuntimeException("Customer cannot be deleted because it is used in contracts");
        }

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
