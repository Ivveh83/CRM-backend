package ivar.hogblom.crmbackend.crm.service.contract;

import ivar.hogblom.crmbackend.dto.contract.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.contract.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.contract.ContractRequestDto;
import ivar.hogblom.crmbackend.crm.entity.customer.Customer;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;

import java.util.List;
import java.util.UUID;

public interface ContractService {

    List<ContractResponseDto> findAll();
    ContractResponseDto findById(UUID id);
    void createContract(ContractRequestDto request);
    void createMultipleContracts(List<ContractRequestDto> dtos);
    void updateContract(UUID id, ContractRequestDto request);
    void updateContractActive(UUID id, ContractActiveUpdateDto dto);
    void renewContract(UUID id, ContractRenewalDto dto);
    void handleSubscriptionUpdated(Subscription oldSub, Subscription newSub, List<String> subscriptionDiffs);
    void handleSubscriptionDeleted(Subscription oldSub, Subscription toDelete);
    void handleCustomerUpdated(Customer oldC, Customer newC, List<String> diffs);
    void handleResellerUpdated(Reseller oldR, Reseller newR, List<String> diffs);
    void handleResellerDeleted(Reseller oldR, Reseller deletedR);
    void deleteContract(UUID id);
}
