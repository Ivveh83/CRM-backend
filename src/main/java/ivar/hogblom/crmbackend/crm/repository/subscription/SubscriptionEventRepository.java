package ivar.hogblom.crmbackend.crm.repository.subscription;

import ivar.hogblom.crmbackend.crm.entity.subscription.SubscriptionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, UUID> {

    List<SubscriptionEvent> findBySubscriptionIdOrderByEventTsDesc(UUID subscriptionId);
    void deleteAllBySubscriptionId(UUID subscriptionId);

}
