package ivar.hogblom.crmbackend.crm.service.subscription;

import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionRequestDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionResponseDto;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    List<SubscriptionResponseDto> findAll();

    List<SubscriptionForContractComponentsDto> findAllSubscriptionsForContractComponents();

    SubscriptionResponseDto findById(UUID id);

    void createSubscription(SubscriptionRequestDto request);

    void updateSubscription(UUID id, SubscriptionRequestDto request);

    void deleteSubscription(UUID id);

    void updateSubscriptionActive(UUID id, boolean active);
}
