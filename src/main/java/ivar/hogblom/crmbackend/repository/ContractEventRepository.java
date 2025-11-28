package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.ContractEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContractEventRepository extends JpaRepository<ContractEvent, UUID> {
}
