package ivar.hogblom.crmbackend.crm.service.customer;

import ivar.hogblom.crmbackend.dto.customer.CustomerEventDto;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerEventService {

    void logCustomerCreated(Customer c);

    void logCustomerUpdated(Customer newC, List<String> changes);

    void logCustomerSupportNoteUpdated(Customer c, String noteText);

    void logCustomerDeleted(Customer c);

    List<CustomerEventDto> getEventsForCustomer(UUID customerId);

    void deleteEvent(UUID eventId);

    void deleteAllEventsForCustomer(UUID customerId);
}

