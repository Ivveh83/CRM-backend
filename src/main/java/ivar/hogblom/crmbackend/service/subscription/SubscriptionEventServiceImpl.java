package ivar.hogblom.crmbackend.service.subscription;

import ivar.hogblom.crmbackend.dto.subscription.SubscriptionEventDto;
import ivar.hogblom.crmbackend.entity.subscription.Subscription;
import ivar.hogblom.crmbackend.entity.subscription.SubscriptionEvent;
import ivar.hogblom.crmbackend.entity.subscription.SubscriptionEventType;
import ivar.hogblom.crmbackend.repository.subscription.SubscriptionEventRepository;
import ivar.hogblom.crmbackend.repository.subscription.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionEventServiceImpl implements SubscriptionEventService {

    private final SubscriptionEventRepository subscriptionEventRepository;


    private final String CREATED = "Abonnemanget skapades. ";
    private final String DELETED = "Abonnemanget raderades. ";
    private final String PAUSED = "Abonnemanget pausades.";
    private final String REACTIVATED = "Abonnemanget återaktiverades.";

    // ----------------------------- CREATE -----------------------------
    @Override
    @Transactional
    public void logSubscriptionCreated(Subscription s) {
        if (s == null) return;

        subscriptionEventRepository.save(toEntity(s, SubscriptionEventType.SKAPAT, CREATED + s));
    }

    // ----------------------------- UPDATE -----------------------------
    @Override
    @Transactional
    public void logSubscriptionUpdated(Subscription newS, List<String> diffs) {
        if (diffs == null || diffs.isEmpty() || newS == null) return;

        subscriptionEventRepository.save(
                toEntity(newS, SubscriptionEventType.UPPDATERAT, String.join("\n• ", diffs))
        );
    }

    // ----------------------------- PAUSED -----------------------------
    @Override
    @Transactional
    public void logSubscriptionPaused(Subscription s) {
        subscriptionEventRepository.save(toEntity(s, SubscriptionEventType.PAUSAT, PAUSED));
    }

    // ----------------------------- REACTIVATED ------------------------
    @Override
    @Transactional
    public void logSubscriptionReactivated(Subscription s) {
        subscriptionEventRepository.save(toEntity(s, SubscriptionEventType.ÅTERAKTIVERAT, REACTIVATED));
    }

    // ----------------------------- SUPPORT NOTE ------------------------
    @Override
    @Transactional
    public void logSubscriptionSupportNote(Subscription s, String note) {
        subscriptionEventRepository.save(toEntity(s, SubscriptionEventType.SUPPORT_ANTECKNING, note));
    }

    // ----------------------------- DELETE ------------------------------
    @Override
    @Transactional
    public void logSubscriptionDeleted(Subscription s) {
        if (s == null) return;

        subscriptionEventRepository.save(
                SubscriptionEvent.builder()
                        .subscriptionId(s.getId())
                        .eventType(SubscriptionEventType.RADERAT)
                        .detail(DELETED + s)
                        .eventTs(LocalDateTime.now())
                        .actor(fetchActor())
                        .build()
        );
    }

    // ----------------------------- GET EVENTS ---------------------------
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionEventDto> getEventsForSubscription(UUID id) {
        return subscriptionEventRepository.findBySubscriptionIdOrderByEventTsDesc(id)
                .stream()
                .map(e -> SubscriptionEventDto.builder()
                        .id(e.getId())
                        .eventType(e.getEventType().name())
                        .detail(e.getDetail())
                        .eventTs(e.getEventTs())
                        .actor(e.getActor())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {

        if (!subscriptionEventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found: " + eventId);
        }

        subscriptionEventRepository.deleteById(eventId);
    }

    @Override
    @Transactional
    public void deleteAllEventsForSubscription(UUID subscriptionId) {

        subscriptionEventRepository.deleteAllBySubscriptionId(subscriptionId);
    }


    // ----------------------------- HELPER -------------------------------
    private static String fetchActor() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null ? a.getName() : "System";
    }

    private static SubscriptionEvent toEntity(Subscription s, SubscriptionEventType type, String detail) {
        return SubscriptionEvent.builder()
                .subscriptionId(s.getId())
                .eventType(type)
                .detail(detail)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();
    }
}
