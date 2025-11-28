package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.*;
import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.Customer;
import ivar.hogblom.crmbackend.entity.Reseller;
import ivar.hogblom.crmbackend.entity.Subscription;
import ivar.hogblom.crmbackend.repository.ContractRepository;
import ivar.hogblom.crmbackend.repository.CustomerRepository;
import ivar.hogblom.crmbackend.repository.ResellerRepository;
import ivar.hogblom.crmbackend.repository.SubscriptionRepository;
import ivar.hogblom.crmbackend.util.ContractCloneUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ResellerRepository resellerRepository;
    private final CustomerRepository customerRepository;
    private final ContractEventService contractEventService;
    private final ContractCloneUtil contractCloneUtil;

    public ContractServiceImpl(ContractRepository contractRepository,
                               SubscriptionRepository subscriptionRepository,
                               ResellerRepository resellerRepository,
                               CustomerRepository customerRepository,
                               ContractEventService contractEventService,
                               ContractCloneUtil contractCloneUtil) {
        this.contractRepository = contractRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.resellerRepository = resellerRepository;
        this.customerRepository = customerRepository;
        this.contractEventService = contractEventService;
        this.contractCloneUtil = contractCloneUtil;
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

    @Override
    public void createContract(ContractRequestDto dto) {

        // --- Validera datumen ---
        validateContractDates(dto);

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

        contractRepository.save(contract);
    }

    @Override
    public void updateContract(UUID id, ContractRequestDto dto) {

        //Validate that contract entity already exists in the database
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract with id " + id + " not found!"));

        //Validate dates
        validateContractDates(dto);

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

        Contract oldCopy = ContractCloneUtil.cloneContract(existing);

        //Set new values + id and save updated contract
        Contract entity = toEntity(dto, customer, resellers, subscriptions);
        entity.setId(id);
        Contract updatedContract = contractRepository.save(entity);

        //Log the diffs
        contractEventService.handleContractUpdate(oldCopy, updatedContract);
        }

    @Override
    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract with id " + id + " not found!"));
        contractRepository.delete(contract);
    }

    public void updateContractActive(UUID id, ContractActiveUpdateDto dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        boolean newActive = dto.active();
        contract.setActive(newActive);
        String detail = dto.detail();
        Contract updatedContract = contractRepository.save(contract);

        contractEventService.handleContractActiveUpdate(updatedContract, newActive, detail);
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
