package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Subscription findByName(String name);

}
