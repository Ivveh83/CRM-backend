package ivar.hogblom.crmbackend.crm.repository.contract;

import ivar.hogblom.crmbackend.crm.entity.contract.ContractEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContractEventRepository extends JpaRepository<ContractEvent, UUID> {
    List<ContractEvent> findByContractIdOrderByEventTsDesc(UUID contractId);
    void deleteAllByContractId(UUID contractId);
}
