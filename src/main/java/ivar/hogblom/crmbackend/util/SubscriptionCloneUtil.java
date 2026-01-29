package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCloneUtil {

    public Subscription clone(Subscription s) {
        if (s == null) return null;

        return Subscription.builder()
                .id(s.getId()) // ID tas med för jämförelse
                .name(s.getName())
                .category(s.getCategory())
                .description(s.getDescription())
                .serviceLevel(s.getServiceLevel())
                .pricePerMonth(s.getPricePerMonth())
                .contractLength(s.getContractLength())
                .renewalPeriod(s.getRenewalPeriod())
                .active(s.getActive())
                .supportContact(s.getSupportContact())
                .createdAt(s.getCreatedAt())
                .notes(s.getNotes())
                .build();
    }
}
