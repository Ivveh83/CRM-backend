package ivar.hogblom.crmbackend.crm.service.customer;

import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<CustomerListResponseDto> findAllCustomersForCustomerListComponent();
    List<CustomerForContractComponentsDto> findAllCustomersForContractComponents();
    List<CustomerResponseDto> findAll();
    void createCustomer(CustomerRequestDto customerRequestDto);
    CustomerResponseDto findById(UUID id);
    void updateCustomer(UUID id, CustomerRequestDto customerRequestDto);
    void deleteCustomer(UUID id);

}
