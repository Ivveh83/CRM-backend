package ivar.hogblom.crmbackend.crm.repository.subscription;

import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    boolean existsByName(String name);

}
