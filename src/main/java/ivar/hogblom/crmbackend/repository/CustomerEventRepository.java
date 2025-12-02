package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.CustomerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerEventRepository extends JpaRepository<CustomerEvent, UUID> {
    List<CustomerEvent> findByCustomerIdOrderByEventTsDesc(UUID customerId);
    //select c from CustomerEvent c where c.customerId = :customerId order by c.eventTs DESC
}

