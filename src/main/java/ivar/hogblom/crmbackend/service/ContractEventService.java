package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ContractEventDto;
import ivar.hogblom.crmbackend.dto.ContractRenewalDto;
import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.Customer;
import ivar.hogblom.crmbackend.entity.Reseller;
import ivar.hogblom.crmbackend.entity.Subscription;
import ivar.hogblom.crmbackend.service.dto.ContractPriceChange;

import java.util.List;
import java.util.UUID;

public interface ContractEventService {

    void logContractCreated(Contract newC);
    void logContractUpdate(Contract oldC, Contract newC);
    void logContractActiveUpdate(Contract newC, boolean newActive, String details);
    void logContractRenewal(Contract oldC, ContractRenewalDto renewalDto);
    void logContractDeleted(UUID oldCId, String oldCCustomerOrgNo);
    void logSubscriptionActiveUpdate(Subscription subscription);
    void logSubscriptionUpdate(
            Subscription oldS,
            Subscription newS,
            List<String> subscriptionDiffs,
            List<ContractPriceChange> priceChanges,
            List<Contract> allAffectedContracts
    );
    void logSubscriptionDeleted(Subscription oldS,
                                List<ContractPriceChange> priceChanges,
                                List<Contract> contracts);
    void logCustomerUpdate(
            Customer oldC,
            Customer newC,
            List<String> diffs,
            List<Contract> contracts
    );
    void logResellerUpdate(
            Reseller oldR,
            Reseller newR,
            List<String> diffs,
            List<Contract> contracts
    );
    void logResellerActiveUpdate(Reseller r);
    void logResellerDeleted(Reseller deleted, List<Contract> contracts);
    List<ContractEventDto> getEventsForContract(UUID contractId);

}
