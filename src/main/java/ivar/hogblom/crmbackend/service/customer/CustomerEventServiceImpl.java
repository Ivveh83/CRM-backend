package ivar.hogblom.crmbackend.service.customer;

import ivar.hogblom.crmbackend.dto.customer.CustomerEventDto;
import ivar.hogblom.crmbackend.entity.customer.Customer;
import ivar.hogblom.crmbackend.entity.customer.CustomerEvent;
import ivar.hogblom.crmbackend.entity.customer.CustomerEventType;
import ivar.hogblom.crmbackend.repository.customer.CustomerEventRepository;
import ivar.hogblom.crmbackend.repository.customer.CustomerRepository;
import ivar.hogblom.crmbackend.util.CustomerDiffUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerEventServiceImpl implements CustomerEventService {

    private final String CREATED = "Kunden skapades.";
    private final String DELETED = "Kunden raderades.";

    private final CustomerEventRepository customerEventRepository;
    private final CustomerRepository customerRepository;
    private final CustomerDiffUtil customerDiffUtil;


    // -----------------------------------------------------
    // 🔵 CREATED
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logCustomerCreated(Customer newC) {

        String details = newC.toString();

        CustomerEvent event = toEntity(
                newC,
                CustomerEventType.SKAPAD,
                details
        );

        customerEventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 UPDATED
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logCustomerUpdated(Customer newC, List<String> changes) {

        if (changes == null || changes.isEmpty() || newC == null){
            return;
        }

        String details = String.join("\n• ", changes);

        CustomerEvent event = toEntity(
                newC,
                CustomerEventType.UPPDATERAD,
                details
        );

        customerEventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 SUPPORT NOTE UPDATED
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logCustomerSupportNoteUpdated(Customer customer, String noteText) {

        CustomerEvent event = toEntity(
                customer,
                CustomerEventType.SUPPORT_ANTECKNING,
                noteText
        );

        customerEventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 DELETED
    // -----------------------------------------------------
    @Transactional
    @Override
    public void logCustomerDeleted(Customer customer) {

        String details = customer.toString();
        CustomerEvent event = CustomerEvent.builder()
                .customerId(customer.getId())
                .eventType(CustomerEventType.RADERAD)
                .detail(details)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();

        customerEventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 GET ALL EVENTS FOR A CUSTOMER
    // -----------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CustomerEventDto> getEventsForCustomer(UUID customerId) {

        return customerEventRepository.findByCustomerIdOrderByEventTsDesc(customerId)
                .stream()
                .map(event -> CustomerEventDto.builder()
                        .id(event.getId())
                        .eventType(event.getEventType().name())
                        .detail(event.getDetail())
                        .eventTs(event.getEventTs())
                        .actor(event.getActor())
                        .build()
                )
                .toList();
    }

    // -----------------------------------------------------
    // 🔴 DELETE SINGLE EVENT
    // -----------------------------------------------------
    @Override
    @Transactional
    public void deleteEvent(UUID eventId) {

        if (!customerEventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Customer event not found: " + eventId);
        }

        customerEventRepository.deleteById(eventId);
    }

    // -----------------------------------------------------
    // 🔴 DELETE ALL EVENTS
    // -----------------------------------------------------
    @Override
    @Transactional
    public void deleteAllEventsForCustomer(UUID customerId) {
        customerEventRepository.deleteAllByCustomerId(customerId);
    }


    // -----------------------------------------------------
    // 🔵 HELPER METHODS
    // -----------------------------------------------------

    private static Authentication fetchAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static String fetchActor() {
        Authentication authentication = fetchAuth();
        return authentication != null ? authentication.getName() : "System";
    }

    public static CustomerEvent toEntity(
            Customer customer,
            CustomerEventType eventType,
            String detail
    ) {
        return CustomerEvent.builder()
                .customerId(customer.getId())
                .eventType(eventType)
                .detail(detail)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();
    }
}
