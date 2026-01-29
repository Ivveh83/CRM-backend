package ivar.hogblom.crmbackend.crm.service.contract;

import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.crm.service.contract.ContractValidatingServiceImpl;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ContractValidatingServiceImplTest {

    @Mock
    ContractService nextContractService;

    ContractValidatingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContractValidatingServiceImpl(nextContractService);
    }

    @Test
    void createContract_withDueDateBeforeContractDate_shouldThrowException() {
        // --- Arrange ---
        ContractRequestDto dto = ContractRequestDto.builder()
                .subscriptionIds(List.of(UUID.randomUUID()))
                .resellerIds(List.of(UUID.randomUUID()))
                .customerId(UUID.randomUUID())
                .contractDate(LocalDate.of(2025, 1, 10))
                .dueDate(LocalDate.of(2025, 1, 5)) // ❌ före contractDate
                .renewalDates(List.of())
                .totalPricePerMonth(1000.0)
                .status(true)
                .active(true)
                .contractLengthMonths(12)
                .comment("test")
                .build();

        // --- Act + Assert ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createContract(dto)
        );

        assertEquals(
                "Due date cannot be before contract date",
                ex.getMessage()
        );


        // --- Assert ---
        verifyNoInteractions(nextContractService);
    }

    @Test
    void createContract_withRenewalDateOutsideContractPeriod_shouldThrowException() {
        // --- Arrange ---
        ContractRequestDto dto = ContractRequestDto.builder()
                .subscriptionIds(List.of(UUID.randomUUID()))
                .resellerIds(List.of(UUID.randomUUID()))
                .customerId(UUID.randomUUID())
                .contractDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 12, 31)) // giltig
                .renewalDates(List.of(
                        LocalDate.of(2024, 12, 31) // ❌ före contractDate
                ))
                .totalPricePerMonth(1000.0)
                .status(true)
                .active(true)
                .contractLengthMonths(12) // giltig
                .comment("test")
                .build();

        // --- Act ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createContract(dto)
        );

        // --- Assert ---
        assertEquals(
                "All renewal dates must be between contractDate and dueDate",
                ex.getMessage()
        );

        verifyNoInteractions(nextContractService);
    }

    @Test
    void createContract_withTooShortContractLength_shouldThrowException() {
        // --- Arrange ---
        ContractRequestDto dto = ContractRequestDto.builder()
                .subscriptionIds(List.of(UUID.randomUUID()))
                .resellerIds(List.of(UUID.randomUUID()))
                .customerId(UUID.randomUUID())
                .contractDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2026, 1, 1)) // 12 månader
                .renewalDates(List.of())           // giltigt
                .totalPricePerMonth(1000.0)
                .status(true)
                .active(true)
                .contractLengthMonths(6)           // ❌ för kort
                .comment("test")
                .build();

        // --- Act ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createContract(dto)
        );

        // --- Assert ---
        assertEquals(
                "Contract length in months cannot be shorter than the period between contractDate and dueDate",
                ex.getMessage()
        );

        verifyNoInteractions(nextContractService);
    }

}
