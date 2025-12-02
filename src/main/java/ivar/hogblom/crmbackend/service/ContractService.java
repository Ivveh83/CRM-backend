package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.ContractRenewalDto;
import ivar.hogblom.crmbackend.dto.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.ContractRequestDto;
import ivar.hogblom.crmbackend.entity.Customer;
import ivar.hogblom.crmbackend.entity.Reseller;
import ivar.hogblom.crmbackend.entity.Subscription;

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
