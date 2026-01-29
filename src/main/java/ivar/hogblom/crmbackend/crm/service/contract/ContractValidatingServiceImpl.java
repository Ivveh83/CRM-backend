package ivar.hogblom.crmbackend.crm.service.contract;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import ivar.hogblom.crmbackend.dto.contract.ContractResponseDto;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RequireCrmDatabase
@Service("validatingContractService")
@Transactional(transactionManager = "crmTransactionManager")
public class ContractValidatingServiceImpl  implements ContractService {

    private final ContractService nextContractService;

    public ContractValidatingServiceImpl(
            @Qualifier("mainContractService") ContractService contractService) {
        this.nextContractService = contractService;
    }


    @Override
    public List<ContractResponseDto> findAll() {
        return nextContractService.findAll();
    }

    @Override
    public ContractResponseDto findById(UUID id) {

        return nextContractService.findById(id);
    }

    @Override
    public void createContract(ContractRequestDto request) {

        validateContractDates(request);
        nextContractService.createContract(request);
    }

    @Override
    public void updateContract(UUID id, ContractRequestDto request) {
        validateContractDates(request);
        nextContractService.updateContract(id, request);
    }

    @Override
    public void updateContractActive(UUID id, ContractActiveUpdateDto dto) {
        nextContractService.updateContractActive(id, dto);
    }

    @Override
    public void renewContract(UUID id, ContractRenewalDto dto) {
        nextContractService.renewContract(id, dto);
    }

    @Override
    public void handleSubscriptionUpdated(Subscription oldSub, Subscription newSub, List<String> subscriptionDiffs) {
        nextContractService.handleSubscriptionUpdated(oldSub, newSub, subscriptionDiffs);
    }

    @Override
    public void handleSubscriptionDeleted(Subscription oldSub, Subscription toDelete) {
        nextContractService.handleSubscriptionDeleted(oldSub, toDelete);
    }

    @Override
    public void handleCustomerUpdated(Customer oldC, Customer newC, List<String> diffs) {
        nextContractService.handleCustomerUpdated(oldC, newC, diffs);
    }

    @Override
    public void handleResellerUpdated(Reseller oldR, Reseller newR, List<String> diffs) {
        nextContractService.handleResellerUpdated(oldR, newR, diffs);
    }

    @Override
    public void handleResellerDeleted(Reseller oldR, Reseller deletedR) {
        nextContractService.handleResellerDeleted(oldR, deletedR);
    }

    @Override
    public void deleteContract(UUID id) {
        nextContractService.deleteContract(id);
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
