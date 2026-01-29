package ivar.hogblom.crmbackend.crm.service.customer;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerListResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import ivar.hogblom.crmbackend.util.OrganizationNumberValidator;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RequireCrmDatabase
@Transactional(transactionManager = "crmTransactionManager")
@Service("validatingCustomerService")
public class CustomerServiceValidatingImpl implements CustomerService {

    private final CustomerService delegate;
    private final OrganizationNumberValidator orgNoValidator;

    @Autowired
    public CustomerServiceValidatingImpl(
            @Qualifier("mainCustomerService")CustomerService delegate,
            OrganizationNumberValidator orgNoValidator
    )
        {
        this.delegate = delegate;
        this.orgNoValidator = orgNoValidator;
        }

    @Override
    public List<CustomerListResponseDto> findAllCustomersForCustomerListComponent() {
        return delegate.findAllCustomersForCustomerListComponent();
    }

    @Override
    public List<CustomerForContractComponentsDto> findAllCustomersForContractComponents() {
        return delegate.findAllCustomersForContractComponents();
    }

    @Override
    public void createCustomer(CustomerRequestDto customerRequestDto) {

        if (!orgNoValidator.isValid(customerRequestDto.orgNo())) {
            throw new ValidationException("orgNo is not valid");
        }
        delegate.createCustomer(customerRequestDto);
    }

    @Override
    public List<CustomerResponseDto> findAll() {
        return delegate.findAll();
    }

    @Override
    public CustomerResponseDto findById(UUID id) {
        return delegate.findById(id);
    }

    @Override
    public void updateCustomer(UUID id, CustomerRequestDto customerRequestDto) {

        if (!orgNoValidator.isValid(customerRequestDto.orgNo())) {
            throw new ValidationException("orgNo is not valid");
        }
        delegate.updateCustomer(id, customerRequestDto);
    }

    @Override
    public void deleteCustomer(UUID id) {
        delegate.deleteCustomer(id);
    }
}
