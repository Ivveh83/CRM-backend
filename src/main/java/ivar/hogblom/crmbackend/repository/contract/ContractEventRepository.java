package ivar.hogblom.crmbackend.repository.contract;

import ivar.hogblom.crmbackend.entity.contract.ContractEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractEventRepository extends JpaRepository<ContractEvent, UUID> {
    List<ContractEvent> findByContractIdOrderByEventTsDesc(UUID contractId);
    void deleteAllByContractId(UUID contractId);
}
