package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.SubscriptionEventDto;
import ivar.hogblom.crmbackend.entity.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionEventService {

    void logSubscriptionCreated(Subscription s);

    void logSubscriptionUpdated(Subscription newS, List<String> diffs);

    void logSubscriptionPaused(Subscription s);

    void logSubscriptionReactivated(Subscription s);

    void logSubscriptionSupportNote(Subscription s, String note);

    void logSubscriptionDeleted(Subscription s);

    List<SubscriptionEventDto> getEventsForSubscription(UUID subscriptionId);
}
