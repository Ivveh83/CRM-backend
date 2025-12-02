package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ResellerEventDto;
import ivar.hogblom.crmbackend.entity.*;
import ivar.hogblom.crmbackend.repository.ResellerEventRepository;
import ivar.hogblom.crmbackend.util.ResellerDiffUtil;
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
public class ResellerEventServiceImpl implements ResellerEventService {

    private final ResellerEventRepository eventRepository;
    private final ResellerDiffUtil resellerDiffUtil;

    private final String CREATED = "Återförsäljaren skapades. ";
    private final String DELETED = "Återförsäljaren raderades. ";
    private final String PAUSED = "Återförsäljaren pausades.";
    private final String REACTIVATED = "Återförsäljaren återaktiverades.";

    // ----------------------------- CREATE -----------------------------
    @Override
    public void logResellerCreated(Reseller r) {
        if (r == null) {
            return;
        }
        eventRepository.save(toEntity(r, ResellerEventType.SKAPAT, CREATED + r));
    }

    // ----------------------------- UPDATE -----------------------------
    @Override
    @Transactional
    public void logResellerUpdated(Reseller newR, List<String> diffs) {
        if (diffs == null || diffs.isEmpty() || newR == null) return;

        eventRepository.save(
                toEntity(newR, ResellerEventType.UPPDATERAT, String.join("\n• ", diffs))
        );
    }

    // ----------------------------- PAUSE ------------------------------
    @Override
    public void logResellerPaused(Reseller r) {
        eventRepository.save(toEntity(r, ResellerEventType.PAUSAT, PAUSED));
    }

    // ----------------------------- REACTIVATE -------------------------
    @Override
    public void logResellerReactivated(Reseller r) {
        eventRepository.save(toEntity(r, ResellerEventType.ÅTERAKTIVERAT, REACTIVATED));
    }

    // ----------------------------- SUPPORT NOTE ------------------------
    @Override
    public void logResellerSupportNote(Reseller r, String note) {
        eventRepository.save(toEntity(r, ResellerEventType.SUPPORT_ANTECKNING, note));
    }

    // ----------------------------- DELETE ------------------------------
    @Override
    public void logResellerDeleted(Reseller r) {
        eventRepository.save(
                ResellerEvent.builder()
                        .resellerId(r.getId())
                        .eventType(ResellerEventType.RADERAT)
                        .detail(DELETED + r)
                        .eventTs(LocalDateTime.now())
                        .actor(fetchActor())
                        .build()
        );
    }

    // ----------------------------- GET EVENTS ---------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ResellerEventDto> getEventsForReseller(UUID id) {
        return eventRepository.findByResellerIdOrderByEventTsDesc(id)
                .stream()
                .map(e -> ResellerEventDto.builder()
                        .id(e.getId())
                        .eventType(e.getEventType().name())
                        .detail(e.getDetail())
                        .eventTs(e.getEventTs())
                        .actor(e.getActor())
                        .build())
                .toList();
    }

    // ----------------------------- HELPER -------------------------------
    private static String fetchActor() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null ? a.getName() : "System";
    }

    private static ResellerEvent toEntity(Reseller r, ResellerEventType type, String detail) {
        return ResellerEvent.builder()
                .resellerId(r.getId())
                .eventType(type)
                .detail(detail)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();
    }
}
