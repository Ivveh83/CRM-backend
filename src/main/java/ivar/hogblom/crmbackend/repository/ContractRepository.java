package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findAllByResellers_Id(UUID id);
    //select c from Contract c inner join c.resellers resellers where resellers.id = :id
    List<Contract> findAllBySubscriptions_Id(UUID subscriptionId);
    //select c from Contract c inner join c.subscriptions subscriptions where subscriptions.id = :subscriptionId


}
