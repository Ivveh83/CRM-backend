package ivar.hogblom.crmbackend.crm.service.contract;

import ivar.hogblom.crmbackend.crm.entity.contract.Contract;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import ivar.hogblom.crmbackend.crm.repository.contract.ContractRepository;
import ivar.hogblom.crmbackend.crm.repository.customer.CustomerRepository;
import ivar.hogblom.crmbackend.crm.repository.reseller.ResellerRepository;
import ivar.hogblom.crmbackend.crm.repository.subscription.SubscriptionRepository;
import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    ContractRepository contractRepository;
    @Mock
    SubscriptionRepository subscriptionRepository;
    @Mock
    ResellerRepository resellerRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock ContractEventService contractEventService;

    ContractServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContractServiceImpl(
                contractRepository,
                subscriptionRepository,
                resellerRepository,
                customerRepository,
                contractEventService
        );
    }

    //Helper method
    private ContractRequestDto mockContractRequest(
            UUID customerId,
            List<UUID> resellerIds,
            List<UUID> subscriptionIds
    ) {
        return ContractRequestDto.builder()
                .customerId(customerId)
                .resellerIds(resellerIds)
                .subscriptionIds(subscriptionIds)
                .active(true)
                .contractDate(LocalDate.now())
                .dueDate(LocalDate.now().plusMonths(12))
                .contractLengthMonths(12)
                .build();
    }


    @Test
    void createContract_shouldSaveContract_andLogEvent() {
        UUID customerId = UUID.randomUUID();
        UUID resellerId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();

        ContractRequestDto dto = mockContractRequest(
                customerId,
                List.of(resellerId),
                List.of(subscriptionId)
        );

        Customer customer = new Customer();
        Reseller reseller = new Reseller();
        Subscription subscription = new Subscription();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));
        when(resellerRepository.findAllById(List.of(resellerId)))
                .thenReturn(List.of(reseller));
        when(subscriptionRepository.findAllById(List.of(subscriptionId)))
                .thenReturn(List.of(subscription));
        when(contractRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        service.createContract(dto);

        verify(contractRepository).save(any(Contract.class));
        verify(contractEventService).logContractCreated(any(Contract.class));
    }

    @Test
    void createContract_shouldThrow_whenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        ContractRequestDto dto = mockContractRequest(
                customerId,
                List.of(),
                List.of()
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.createContract(dto));

        verify(contractRepository, never()).save(any());
        verifyNoInteractions(contractEventService);
    }

    @Test
    void createContract_shouldThrow_whenResellerMissing() {
        UUID customerId = UUID.randomUUID();
        UUID resellerId = UUID.randomUUID();

        ContractRequestDto dto = mockContractRequest(
                customerId,
                List.of(resellerId),
                List.of()
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(new Customer()));
        when(resellerRepository.findAllById(List.of(resellerId)))
                .thenReturn(List.of()); // mismatch

        assertThrows(ValidationException.class,
                () -> service.createContract(dto));
    }

    @Test
    void createContract_shouldThrow_whenSubscriptionMissing() {
        UUID customerId = UUID.randomUUID();
        UUID resellerId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();

        ContractRequestDto dto = mockContractRequest(
                customerId,
                List.of(resellerId),
                List.of(subscriptionId)
        );


        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(new Customer()));
        when(resellerRepository.findAllById(List.of(resellerId)))
                .thenReturn(List.of(new Reseller()));
        when(subscriptionRepository.findAllById(List.of(subscriptionId)))
                .thenReturn(List.of()); // mismatch

        assertThrows(ValidationException.class,
                () -> service.createContract(dto));
    }

    @Test
    void updateContract_shouldSaveContractandLogUpdateEvent() {
        UUID contractId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Contract existing = new Contract();
        existing.setId(contractId);

        ContractRequestDto dto = mockContractRequest(
                customerId,
                List.of(),
                List.of()
        );

        when(contractRepository.findById(contractId))
                .thenReturn(Optional.of(existing));
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(new Customer()));
        when(resellerRepository.findAllById(any()))
                .thenReturn(List.of());
        when(subscriptionRepository.findAllById(any()))
                .thenReturn(List.of());
        when(contractRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        service.updateContract(contractId, dto);

        verify(contractRepository).save(any(Contract.class));

        verify(contractEventService)
                .logContractUpdate(any(Contract.class), any(Contract.class));
    }

    // Unity tests for exception throwing would duplicate create function, so they are not necessary here.

    @Test
    void updateContractActive_shouldUpdateAndLog() {
        // --- Arrange ---
        UUID id = UUID.randomUUID();
        Contract contract = new Contract();
        contract.setId(id);
        contract.setActive(false);  // ursprungligt värde

        ContractActiveUpdateDto dto = new ContractActiveUpdateDto(true, "activated");

        // Mock repository
        when(contractRepository.findById(eq(id))).thenReturn(Optional.of(contract));
        // Mock save: returnera samma kontrakt som skickas in
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

        // --- Act ---
        service.updateContractActive(id, dto);

        // --- Assert ---
        // Kontrollera att contract nu har active=true
        assertTrue(contract.isActive());

        // Kontrollera att repository.save anropades med kontraktet
        verify(contractRepository).save(contract);

        // Kontrollera att eventService loggades med rätt värden
        verify(contractEventService).logContractActiveUpdate(contract, true, "activated");
    }

    @Test
    void renewContract_shouldUpdateAndLog() {
        // --- Arrange ---
        UUID id = UUID.randomUUID();

        Contract contract = new Contract();
        contract.setId(id);

        LocalDate newDueDate = LocalDate.now().plusMonths(12);
        List<LocalDate> renewalDates = List.of(LocalDate.now());
        boolean status = true;

        ContractRenewalDto dto = new ContractRenewalDto(
                newDueDate,
                renewalDates,
                status
        );

        when(contractRepository.findById(id))
                .thenReturn(Optional.of(contract));

        // returnera samma instans som skickas in
        when(contractRepository.save(any(Contract.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // --- Act ---
        service.renewContract(id, dto);

        // --- Assert ---
        assertEquals(newDueDate, contract.getDueDate());
        assertEquals(renewalDates, contract.getRenewalDates());
        assertEquals(status, contract.isStatus());

        verify(contractRepository).save(contract);
        verify(contractEventService).logContractRenewal(contract, dto);
    }


    @Test
    void handleSubscriptionUpdated_shouldUpdateContractPriceAndSaveAndLogUpdate() {
        Subscription oldSub = new Subscription();
        oldSub.setId(UUID.randomUUID());
        oldSub.setPricePerMonth(100.0);

        Subscription newSub = new Subscription();
        newSub.setId(oldSub.getId());
        newSub.setPricePerMonth(200.0);

        Contract contract = new Contract();
        contract.setSubscriptions(List.of(newSub));
        contract.setTotalPricePerMonth(100.0);

        when(contractRepository.findAllBySubscriptions_Id(newSub.getId()))
                .thenReturn(List.of(contract));

        service.handleSubscriptionUpdated(
                oldSub,
                newSub,
                List.of("price changed")
        );

        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);

        verify(contractRepository).save(captor.capture());

        Contract savedContract = captor.getValue();
        assertEquals(200.0, savedContract.getTotalPricePerMonth());

        verify(contractEventService)
                .logSubscriptionUpdate(
                        eq(oldSub),
                        eq(newSub),
                        anyList(),
                        anyList(),
                        anyList()
                );
    }

    @Test
    void handleSubscriptionDeleted_shouldRemoveSubFromContractsAndUpdateTotalPricePerMonthAndSaveContractsAndLogEvent() {
        Subscription toDelete = new Subscription();
        toDelete.setId(UUID.randomUUID());
        toDelete.setPricePerMonth(100.0);

        Subscription other = new Subscription();
        other.setId(UUID.randomUUID());
        other.setPricePerMonth(50.0);

        Contract contract = new Contract();
        contract.setSubscriptions(new ArrayList<>(List.of(toDelete, other)));
        contract.setTotalPricePerMonth(150.0);

        when(contractRepository.findAllBySubscriptions_Id(toDelete.getId()))
                .thenReturn(List.of(contract));

        service.handleSubscriptionDeleted(toDelete, toDelete);

        // prenumerationen är borttagen
        assertFalse(
                contract.getSubscriptions().stream()
                        .anyMatch(s -> s.getId().equals(toDelete.getId()))
        );

        // priset är uppdaterat
        assertEquals(50.0, contract.getTotalPricePerMonth());

        verify(contractRepository).save(contract);
        verify(contractEventService).logSubscriptionDeleted(
                eq(toDelete),
                anyList(),
                eq(List.of(contract))
        );
    }

    @Test
    void handleCustomerUpdated_withNoDiffs_shouldDoNothing() {
        // --- Arrange ---
        Customer oldCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .companyName("Old Co")
                .orgNo("123")
                .build();

        Customer newCustomer = Customer.builder()
                .id(oldCustomer.getId())
                .companyName("New Co")
                .orgNo("123")
                .build();

        // --- Act ---
        service.handleCustomerUpdated(oldCustomer, newCustomer, List.of());
        service.handleCustomerUpdated(oldCustomer, newCustomer, null);

        // --- Assert ---
        verifyNoInteractions(contractRepository);
        verifyNoInteractions(contractEventService);
    }


    @Test
    void handleCustomerUpdated_withDiffs_shouldLogEvent() {
        // --- Arrange ---
        UUID customerId = UUID.randomUUID();

        Customer oldCustomer = Customer.builder()
                .id(customerId)
                .companyName("Old Co")
                .orgNo("123")
                .build();

        Customer newCustomer = Customer.builder()
                .id(customerId)
                .companyName("New Co")
                .orgNo("123")
                .build();

        Contract contract = new Contract();
        contract.setId(UUID.randomUUID());
        contract.setCustomer(newCustomer);

        List<String> diffs = List.of("companyName");

        when(contractRepository.findAllByCustomer_Id(customerId))
                .thenReturn(List.of(contract));

        // --- Act ---
        service.handleCustomerUpdated(oldCustomer, newCustomer, diffs);

        // --- Assert ---
        verify(contractEventService).logCustomerUpdate(
                oldCustomer,
                newCustomer,
                diffs,
                List.of(contract)
        );
    }

    @Test
    void deleteContract_shouldDeleteAndLog() {
        UUID id = UUID.randomUUID();
        Contract contract = new Contract();
        Customer customer = new Customer();
        customer.setOrgNo("123");

        contract.setId(id);
        contract.setCustomer(customer);

        when(contractRepository.findById(id))
                .thenReturn(Optional.of(contract));

        service.deleteContract(id);

        verify(contractRepository).delete(contract);
        verify(contractEventService)
                .logContractDeleted(id, "123");
    }




}
