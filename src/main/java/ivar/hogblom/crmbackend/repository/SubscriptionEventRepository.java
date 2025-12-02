package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.SubscriptionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, UUID> {

    List<SubscriptionEvent> findBySubscriptionIdOrderByEventTsDesc(UUID subscriptionId);
}
