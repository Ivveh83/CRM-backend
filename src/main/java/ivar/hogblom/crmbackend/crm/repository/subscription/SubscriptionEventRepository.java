package ivar.hogblom.crmbackend.crm.repository.subscription;

import ivar.hogblom.crmbackend.crm.entity.subscription.SubscriptionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, UUID> {

    List<SubscriptionEvent> findBySubscriptionIdOrderByEventTsDesc(UUID subscriptionId);
    void deleteAllBySubscriptionId(UUID subscriptionId);

}
