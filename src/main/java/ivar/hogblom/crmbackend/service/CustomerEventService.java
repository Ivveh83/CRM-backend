package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.CustomerEventDto;
import ivar.hogblom.crmbackend.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerEventService {

    void logCustomerCreated(Customer c);

    void logCustomerUpdated(Customer newC, List<String> changes);

    void logCustomerSupportNoteUpdated(Customer c, String noteText);

    void logCustomerDeleted(Customer c);

    List<CustomerEventDto> getEventsForCustomer(UUID customerId);
}

