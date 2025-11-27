package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.CustomerResponseDto;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<CustomerListResponseDto> findAllCustomersForCustomerListComponent();
    List<CustomerForContractComponentsDto> findAllCustomersForContractComponents();
    void createCustomer(CustomerRequestDto customerRequestDto);
    CustomerResponseDto findById(UUID id);
    void updateCustomer(UUID id, CustomerRequestDto customerRequestDto);
    void deleteCustomer(UUID id);

}
