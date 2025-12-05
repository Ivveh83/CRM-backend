package ivar.hogblom.crmbackend.service.contract;

import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import ivar.hogblom.crmbackend.dto.contract.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.customer.CustomerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import ivar.hogblom.crmbackend.entity.contract.Contract;
import ivar.hogblom.crmbackend.entity.customer.Customer;
import ivar.hogblom.crmbackend.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.entity.subscription.Subscription;
import ivar.hogblom.crmbackend.repository.contract.ContractRepository;
import ivar.hogblom.crmbackend.repository.customer.CustomerRepository;
import ivar.hogblom.crmbackend.repository.reseller.ResellerRepository;
import ivar.hogblom.crmbackend.repository.subscription.SubscriptionRepository;
import ivar.hogblom.crmbackend.service.contract.dto.ContractPriceChange;
import ivar.hogblom.crmbackend.util.ContractCloneUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service("mainContractService")
//@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ResellerRepository resellerRepository;
    private final CustomerRepository customerRepository;
    private final ContractEventService contractEventService;

    public ContractServiceImpl(ContractRepository contractRepository,
                               SubscriptionRepository subscriptionRepository,
                               ResellerRepository resellerRepository,
                               CustomerRepository customerRepository,
                               ContractEventService contractEventService) {
        this.contractRepository = contractRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.resellerRepository = resellerRepository;
        this.customerRepository = customerRepository;
        this.contractEventService = contractEventService;
    }

    @Override
    public List<ContractResponseDto> findAll() {
        return contractRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractResponseDto findById(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract with id " + id + " not found!"));
        return toResponseDto(contract);
    }

    @Transactional
    @Override
    public void createContract(ContractRequestDto dto) {

        // ContracValidatingServicempl.createContract(), kolla hur man dekorerar klasser
        // --- Validera datumen ---
        //validateContractDates(dto);

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<UUID> resellerIds = dto.resellerIds();
        List<Reseller> resellers = resellerRepository.findAllById(resellerIds);
        if (resellers.size() != resellerIds.size()) {
            throw new IllegalArgumentException("One or more reseller IDs do not exist");
        }

        List<UUID> subscriptionIds = dto.subscriptionIds();
        List<Subscription> subscriptions = subscriptionRepository.findAllById(subscriptionIds);
        if (subscriptions.size() != subscriptionIds.size()) {
            throw new IllegalArgumentException("One or more subscription IDs do not exist");
        }

        Contract contract = toEntity(dto, customer, resellers, subscriptions);

        Contract savedContract = contractRepository.save(contract);

        contractEventService.logContractCreated(savedContract);
    }

    @Transactional
    @Override
    public void createMultipleContracts(List<ContractRequestDto> dtos) {
        for (ContractRequestDto dto : dtos) {
         createContract(dto);
        }
    }

    @Override
    public void updateContract(UUID id, ContractRequestDto dto) {

        //Validate that contract entity already exists in the database
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract with id " + id + " not found!"));

        //Validate customer exists
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        //Validate resellers exists
        List<UUID> resellerIds = dto.resellerIds();
        List<Reseller> resellers = resellerRepository.findAllById(resellerIds);
        if (resellers.size() != resellerIds.size()) {
            throw new IllegalArgumentException("One or more reseller IDs do not exist");
        }

        //Validate subscriptions exist
        List<UUID> subscriptionIds = dto.subscriptionIds();
        List<Subscription> subscriptions = subscriptionRepository.findAllById(subscriptionIds);
        if (subscriptions.size() != subscriptionIds.size()) {
            throw new IllegalArgumentException("One or more subscription IDs do not exist");
        }

        //Copy for contractEventService
        Contract oldCopy = ContractCloneUtil.cloneContract(existing);

        //Set new values + id and save updated contract
        Contract entity = toEntity(dto, customer, resellers, subscriptions);
        entity.setId(id);
        Contract updatedContract = contractRepository.save(entity);

        //Log the diffs
        contractEventService.logContractUpdate(oldCopy, updatedContract);
        }

    public void updateContractActive(UUID id, ContractActiveUpdateDto dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        boolean newActive = dto.active();
        contract.setActive(newActive);
        String detail = dto.detail();
        Contract updatedContract = contractRepository.save(contract);

        contractEventService.logContractActiveUpdate(updatedContract, newActive, detail);
    }

    @Override
    @Transactional
    public void renewContract(UUID id, ContractRenewalDto dto) {
        Contract existingContract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        existingContract.setDueDate(dto.dueDate());
        existingContract.setRenewalDates(dto.renewalDates());
        existingContract.setStatus(dto.status());

        Contract updatedContract = contractRepository.save(existingContract);

        contractEventService.logContractRenewal(updatedContract, dto);
    }

    @Override
    @Transactional
    public void handleSubscriptionUpdated(Subscription oldSub, Subscription newSub, List<String> subscriptionDiffs) {

        // 1. Hämta alla kontrakt som använder denna sub
        List<Contract> allAffectedContracts =
                contractRepository.findAllBySubscriptions_Id(newSub.getId());

        List<ContractPriceChange> priceChanges = new ArrayList<>();

        if (!Objects.equals(oldSub.getPricePerMonth(), newSub.getPricePerMonth())) {

            for (Contract c : allAffectedContracts) {

                // 1. NUVARANDE total (kan innehålla rabatt)
                double oldSavedTotal =
                        Optional.ofNullable(c.getTotalPricePerMonth()).orElse(0.0);

                // 2. TEORETISK total baserat på gamla priset
                double oldTheoreticalTotal =
                        c.getSubscriptions().stream()
                                .mapToDouble(s -> {
                                    if (s.getId().equals(oldSub.getId())) {
                                        return Optional.ofNullable(oldSub.getPricePerMonth()).orElse(0.0);
                                    }
                                    return Optional.ofNullable(s.getPricePerMonth()).orElse(0.0);
                                })
                                .sum();

                // 3. AVVIKELSE (rabatt / manuella ändringar)
                double deviation = oldSavedTotal - oldTheoreticalTotal;

                // 4. NY teoretisk total baserat på nya priser
                double newTheoreticalTotal =
                        c.getSubscriptions().stream()
                                .mapToDouble(s -> {
                                    if (s.getId().equals(newSub.getId())) {
                                        return Optional.ofNullable(newSub.getPricePerMonth()).orElse(0.0);
                                    }
                                    return Optional.ofNullable(s.getPricePerMonth()).orElse(0.0);
                                })
                                .sum();

                // 5. NY TOTAL, med avvikelsen bevarad
                double newFinalTotal = newTheoreticalTotal + deviation;

                if (!Objects.equals(oldSavedTotal, newFinalTotal)) {

                    c.setTotalPricePerMonth(newFinalTotal);
                    contractRepository.save(c);

                    priceChanges.add(
                            new ContractPriceChange(c, oldSavedTotal, newFinalTotal)
                    );
                }
            }
        }


        // 3. Låt ContractEventService logga händelsen
        contractEventService.logSubscriptionUpdate(
                oldSub,
                newSub,
                subscriptionDiffs,
                priceChanges,
                allAffectedContracts
        );
    }

    @Override
    @Transactional
    public void handleSubscriptionDeleted(Subscription oldSub, Subscription toDelete) {

        List<Contract> contracts =
                contractRepository.findAllBySubscriptions_Id(toDelete.getId());

        List<ContractPriceChange> priceChanges = new ArrayList<>();

        for (Contract c : contracts) {

            double oldSavedTotal =
                    Optional.ofNullable(c.getTotalPricePerMonth()).orElse(0.0);

            double oldTheoretical =
                    c.getSubscriptions().stream()
                            .mapToDouble(s -> Optional.ofNullable(s.getPricePerMonth()).orElse(0.0))
                            .sum();

            double deviation = oldSavedTotal - oldTheoretical;

            // Ta bort abonnemanget från kontraktet
            c.getSubscriptions().removeIf(s -> s.getId().equals(toDelete.getId()));

            double newTheoretical =
                    c.getSubscriptions().stream()
                            .mapToDouble(s -> Optional.ofNullable(s.getPricePerMonth()).orElse(0.0))
                            .sum();

            double newFinal = Math.max(0, newTheoretical + deviation);

            c.setTotalPricePerMonth(newFinal);

            contractRepository.save(c);

            priceChanges.add(new ContractPriceChange(c, oldSavedTotal, newFinal));
        }

        // Logga event
        contractEventService.logSubscriptionDeleted(
                oldSub,
                priceChanges,
                contracts
        );
    }

    @Override
    @Transactional
    public void handleCustomerUpdated(Customer oldCustomer, Customer newCustomer, List<String> customerDiffs) {

        if (customerDiffs == null || customerDiffs.isEmpty()) return;

        // 1. Hitta alla kontrakt som använder kunden
        List<Contract> affectedContracts = contractRepository.findAllByCustomer_Id(newCustomer.getId());

        // 2. Skicka vidare till ContractEventService
        contractEventService.logCustomerUpdate(oldCustomer, newCustomer, customerDiffs, affectedContracts);
    }

    @Override
    @Transactional
    public void handleResellerUpdated(
            Reseller oldR,
            Reseller newR,
            List<String> diffs
    ) {

        if (diffs == null || diffs.isEmpty()) return;

        // 1. Hitta alla kontrakt som använder återförsäljaren
        List<Contract> contracts =
                contractRepository.findAllByResellers_Id(newR.getId());

        // 2. Skicka vidare till event-service
        contractEventService.logResellerUpdate(oldR, newR, diffs, contracts);
    }

    @Override
    @Transactional
    public void handleResellerDeleted(Reseller oldR, Reseller deletedR) {

        // 1. Hämta alla kontrakt som använder återförsäljaren
        List<Contract> contracts =
                contractRepository.findAllByResellers_Id(deletedR.getId());

        // 2. Ta bort resellern från varje kontrakt
        for (Contract c : contracts) {
            c.getResellers().remove(deletedR);
        }

        // 3. Spara uppdaterade kontrakt
        contractRepository.saveAll(contracts);

        // 4. Logga händelser
        contractEventService.logResellerDeleted(
                oldR,
                contracts
        );
    }




    @Override
    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract with id " + id + " not found!"));

        UUID contractId = contract.getId();
        String contractCustomerOrgNo = contract.getCustomer().getOrgNo();

        contractRepository.delete(contract);

        contractEventService.logContractDeleted(contractId, contractCustomerOrgNo);
    }


    /* HELPER METHODS */

    private ContractResponseDto toResponseDto(Contract contract) {

        return ContractResponseDto.builder()
                .id(contract.getId())
                .customer(CustomerForContractComponentsDto.builder()
                        .id(contract.getCustomer().getId())
                        .companyName(contract.getCustomer().getCompanyName())
                        .orgNo(contract.getCustomer().getOrgNo())
                        .build())
                .resellers(
                        contract.getResellers().stream()
                                .map(r ->
                                        ResellerForContractComponentsDto.builder()
                                                .id(r.getId())
                                                .name(r.getName())
                                                .orgNo(r.getOrgNo())
                                                .active(r.isActive())
                                                .build())
                                .toList()
                )
                .subscriptionTypes(
                        contract.getSubscriptions().stream()
                                .map(s ->
                                        SubscriptionForContractComponentsDto.builder()
                                                .id(s.getId())
                                                .name(s.getName())
                                                .contractLength(s.getContractLength())
                                                .renewalPeriod(s.getRenewalPeriod())
                                                .active(s.getActive())
                                                .pricePerMonth(s.getPricePerMonth())
                                                .build()
                                )   // ändra till getType() om det heter något annat
                                .toList()
                )
                .status(contract.isStatus())
                .active(contract.isActive())
                .contractDate(contract.getContractDate())
                .contractLengthMonths(contract.getContractLengthMonths())
                .renewalDates(contract.getRenewalDates())
                .totalPricePerMonth(contract.getTotalPricePerMonth())
                .dueDate(contract.getDueDate())
                .comment(contract.getComment())

                .build();
    }

    private Contract toEntity(
            ContractRequestDto dto,
            Customer customer,
            List<Reseller> resellers,
            List<Subscription> subscriptions
    ) {

        // Beräkna status baserat på dueDate
        LocalDate now = LocalDate.now();
        LocalDate due = dto.dueDate();
        long monthsLeft = ChronoUnit.MONTHS.between(
                now.withDayOfMonth(1),
                due.withDayOfMonth(1)
        );
        boolean status = monthsLeft <= 3;


        return Contract.builder()
                .customer(customer)
                .resellers(resellers)
                .subscriptions(subscriptions)
                .status(status)
                .active(dto.active())
                .contractDate(dto.contractDate())
                .contractLengthMonths(dto.contractLengthMonths())
                .dueDate(dto.dueDate())
                .renewalDates(dto.renewalDates())
                .totalPricePerMonth(dto.totalPricePerMonth())
                .comment(dto.comment())
                .build();
    }

    private void validateContractDates(ContractRequestDto dto) {

        LocalDate contractDate = dto.contractDate();
        List<LocalDate> renewalDates = dto.renewalDates();
        LocalDate dueDate = dto.dueDate();
        Integer lengthMonths = dto.contractLengthMonths();

        // 1. dueDate får inte vara före contractDate
        if (dueDate.isBefore(contractDate)) {
            throw new IllegalArgumentException("Due date cannot be before contract date");
        }

// 2. Alla renewalDates måste ligga inom intervallet [contractDate, dueDate]
        if (renewalDates != null && !renewalDates.isEmpty()) {
            for (LocalDate date : renewalDates) {
                if (date.isBefore(contractDate) || date.isAfter(dueDate)) {
                    throw new IllegalArgumentException(
                            "All renewal dates must be between contractDate and dueDate"
                    );
                }
            }
        }


        // 3. contractLengthMonths måste minst motsvara antalet månader mellan contractDate och dueDate
        long monthsBetween = ChronoUnit.MONTHS.between(
                contractDate.withDayOfMonth(1),
                dueDate.withDayOfMonth(1)
        );

        if (lengthMonths < monthsBetween) {
            throw new IllegalArgumentException(
                    "Contract length in months cannot be shorter than the period between contractDate and dueDate"
            );
        }
    }

}
