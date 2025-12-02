package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.CustomerEventDto;
import ivar.hogblom.crmbackend.entity.Customer;
import ivar.hogblom.crmbackend.entity.CustomerEvent;
import ivar.hogblom.crmbackend.entity.CustomerEventType;
import ivar.hogblom.crmbackend.repository.CustomerEventRepository;
import ivar.hogblom.crmbackend.repository.CustomerRepository;
import ivar.hogblom.crmbackend.util.CustomerDiffUtil;
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

    private final CustomerEventRepository eventRepository;
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

        eventRepository.save(event);
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

        eventRepository.save(event);
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

        eventRepository.save(event);
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

        eventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 GET ALL EVENTS FOR A CUSTOMER
    // -----------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CustomerEventDto> getEventsForCustomer(UUID customerId) {

        return eventRepository.findByCustomerIdOrderByEventTsDesc(customerId)
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
