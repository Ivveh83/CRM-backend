package ivar.hogblom.crmbackend.ai.initializer;

import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.crm.service.customer.CustomerService;
import ivar.hogblom.crmbackend.crm.service.reseller.ResellerService;
import ivar.hogblom.crmbackend.crm.service.subscription.SubscriptionService;
import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerRequestDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerResponseDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class DemoDataService {

    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final ResellerService resellerService;
    @Qualifier("validatingContractService")
    private final ContractService contractService;

    private final List<UUID> customerIds = new ArrayList<>();
    private final List<UUID> subscriptionIds = new ArrayList<>();
    private final List<UUID> resellerIds = new ArrayList<>();
    private final Map<UUID, Integer> customerDifficulty = new HashMap<>();

    @Autowired
    public DemoDataService(CustomerService customerService,
                           SubscriptionService subscriptionService,
                           ResellerService resellerService,
                           @Qualifier("validatingContractService")ContractService contractService
    ) {
        this.customerService = customerService;
        this.subscriptionService = subscriptionService;
        this.resellerService = resellerService;
        this.contractService = contractService;
    }

    @Transactional(transactionManager = "crmTransactionManager")
    public void generateDemoData() {
        createCustomers();
        createSubscriptions();
        createResellers();
        createContractsAndSimulateLifecycle();
    }

    // exakt samma privata metoder som tidigare:
    // createCustomers()
    // createSubscriptions()
    // createResellers()
    // createContractsAndSimulateLifecycle()
    /* -------------------- CUSTOMERS -------------------- */

    private void createCustomers() {
        createCustomer("Trygga Konsult AB", 1);
        createCustomer("Stabil IT Partner", 2);
        createCustomer("Mellanstor Logistik", 3);
        createCustomer("Krävande Mediahus", 4);
        createCustomer("Stökig Byggfirma", 5);
        createCustomer("Katastrofbolaget AB", 6);
    }

    private void createCustomer(String name, int difficulty) {
        CustomerRequestDto dto = CustomerRequestDto.builder()
                .companyName(name)
                .orgNo("55" + (10000000 + difficulty))
                .city("Stockholm")
                .country("SE")
                .createdAt(LocalDate.of(2022, 1, 1))
                .notes(initialCustomerNote(difficulty))
                .build();

        customerService.createCustomer(dto);

        UUID id = customerService
                .findAllCustomersForCustomerListComponent()
                .stream()
                .filter(c -> c.companyName().equals(name))
                .findFirst()
                .orElseThrow()
                .id();

        customerIds.add(id);
        customerDifficulty.put(id, difficulty);

        addCustomerComments(id, difficulty);
    }

    private void addCustomerComments(UUID customerId, int difficulty) {
        for (int i = 1; i <= 4; i++) {
            CustomerResponseDto existing = customerService.findById(customerId);

            CustomerRequestDto updated = CustomerRequestDto.builder()
                    .companyName(existing.companyName())
                    .orgNo(existing.orgNo())
                    .notes(existing.notes() + "\n" + customerComment(difficulty, i))
                    .build();

            customerService.updateCustomer(customerId, updated);
        }
    }

    /* -------------------- SUBSCRIPTIONS -------------------- */

    private void createSubscriptions() {
        createSubscription("Bas", 199.0, 12);
        createSubscription("Standard", 499.0, 12);
        createSubscription("Premium", 999.0, 24);
    }

    private void createSubscription(String name, double price, int length) {
        subscriptionService.createSubscription(
                SubscriptionRequestDto.builder()
                        .name(name)
                        .pricePerMonth(price)
                        .contractLength(length)
                        .active(true)
                        .createdAt(LocalDate.of(2022, 1, 1))
                        .build()
        );

        UUID id = subscriptionService.findAll().stream()
                .filter(s -> s.name().equals(name))
                .findFirst()
                .orElseThrow()
                .id();

        subscriptionIds.add(id);
    }

    /* -------------------- RESELLERS -------------------- */

    private void createResellers() {
        createReseller("Nordic Sales AB");
        createReseller("Enterprise Partners");
    }

    private void createReseller(String name) {
        resellerService.createReseller(
                ResellerRequestDto.builder()
                        .name(name)
                        .orgNo("77" + Math.abs(name.hashCode()))
                        .active(true)
                        .createdAt(LocalDate.of(2022, 1, 1))
                        .build()
        );

        UUID id = resellerService.findAllResellers().stream()
                .filter(r -> r.name().equals(name))
                .findFirst()
                .orElseThrow()
                .id();

        resellerIds.add(id);
    }

    /* -------------------- CONTRACTS -------------------- */

    private void createContractsAndSimulateLifecycle() {
        LocalDate baseDate = LocalDate.of(2022, 3, 1);

        for (int i = 0; i < customerIds.size(); i++) {
            UUID customerId = customerIds.get(i);
            int difficulty = customerDifficulty.get(customerId);

            int contracts = (i < 2) ? 2 : 1;

            for (int c = 0; c < contracts; c++) {
                ContractRequestDto request = ContractRequestDto.builder()
                        .customerId(customerId)
                        .subscriptionIds(List.of(subscriptionIds.get(c % subscriptionIds.size())))
                        .resellerIds(List.of(resellerIds.get(c % resellerIds.size())))
                        .contractDate(baseDate.plusMonths(i * 2L))
                        .dueDate(baseDate.plusMonths(i * 2L + 12))
                        .totalPricePerMonth(500.0 + difficulty * 50)
                        .contractLengthMonths(12)
                        .status(true)
                        .active(true)
                        .comment("Initialt kontrakt tecknat")
                        .build();

                contractService.createContract(request);

                UUID contractId = contractService.findAll().stream()
                        .filter(ct -> ct.customer().id().equals(customerId))
                        .reduce((first, second) -> second)
                        .orElseThrow()
                        .id();

                simulateContractLifecycle(contractId, request, difficulty);
            }
        }
    }

    private void simulateContractLifecycle(UUID contractId,
                                           ContractRequestDto base,
                                           int difficulty) {

        LocalDate date = base.contractDate();

        // 👇 NY RAD – håll reda på aktuellt pris
        double currentPrice = base.totalPricePerMonth();

        for (int i = 1; i <= 14; i++) {
            date = date.plusMonths(1);

            if (difficulty >= 5 && i % 4 == 0) {
                contractService.updateContractActive(
                        contractId,
                        new ContractActiveUpdateDto(false, "Utebliven betalning")
                );
            }
            else if (difficulty >= 5 && i % 5 == 0) {
                contractService.updateContractActive(
                        contractId,
                        new ContractActiveUpdateDto(true, "Betalning mottagen")
                );
            }
            else if (i % 3 == 0) {

                // 👇 ÄNDRA HÄR
                currentPrice += 25;

                ContractRequestDto updated =
                        copyWithNewPrice(base, currentPrice, difficulty);

                contractService.updateContract(contractId, updated);
            }
            else {
                ContractRequestDto updated =
                        copyWithComment(base, contractComment(difficulty, i));

                contractService.updateContract(contractId, updated);
            }
        }
    }


    /* -------------------- HELPERS -------------------- */

    private ContractRequestDto copyWithNewPrice(ContractRequestDto base, double price, int difficulty) {
        return ContractRequestDto.builder()
                .customerId(base.customerId())
                .subscriptionIds(base.subscriptionIds())
                .resellerIds(base.resellerIds())
                .contractDate(base.contractDate())
                .dueDate(base.dueDate())
                .totalPricePerMonth(price)
                .contractLengthMonths(base.contractLengthMonths())
                .status(true)
                .active(true)
                .comment("Prisjustering: " + price + " kr. " + priceComment(difficulty))
                .build();
    }

    private ContractRequestDto copyWithComment(ContractRequestDto base, String comment) {
        return ContractRequestDto.builder()
                .customerId(base.customerId())
                .subscriptionIds(base.subscriptionIds())
                .resellerIds(base.resellerIds())
                .contractDate(base.contractDate())
                .dueDate(base.dueDate())
                .totalPricePerMonth(base.totalPricePerMonth())
                .contractLengthMonths(base.contractLengthMonths())
                .status(true)
                .active(true)
                .comment(comment)
                .build();
    }

    private String initialCustomerNote(int d) {
        return switch (d) {
            case 1 -> "Mycket lojal kund, enkel dialog.";
            case 2 -> "Stabil kundrelation.";
            case 3 -> "Neutral, saklig.";
            case 4 -> "Krävande i dialog.";
            case 5 -> "Ofta missnöjd, sena svar.";
            default -> "Mycket svår kund, eskalerar ofta.";
        };
    }

    private String customerComment(int d, int i) {
        return switch (d) {
            case 1 -> "Tack för snabb hjälp (" + i + ")";
            case 2 -> "Fungerar bra (" + i + ")";
            case 3 -> "Inga synpunkter (" + i + ")";
            case 4 -> "Onödigt krångligt (" + i + ")";
            case 5 -> "Inte nöjd alls (" + i + ")";
            default -> "Helt oacceptabelt (" + i + ")";
        };
    }

    private String contractComment(int d, int i) {
        return switch (d) {
            case 1 -> "Allt fungerar utmärkt (" + i + ")";
            case 2 -> "OK samarbete (" + i + ")";
            case 3 -> "Noterat (" + i + ")";
            case 4 -> "Varför höjs priset? (" + i + ")";
            case 5 -> "Missnöje kring faktura (" + i + ")";
            default -> "Krav på omedelbar åtgärd (" + i + ")";
        };
    }

    private String priceComment(int d) {
        return d <= 3 ? "Kunden accepterade ändringen." : "Kunden ifrågasatte priset.";
    }
}
