package ivar.hogblom.crmbackend.crm.service.reseller;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.reseller.ResellerEventDto;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.crm.entity.reseller.ResellerEvent;
import ivar.hogblom.crmbackend.crm.entity.reseller.ResellerEventType;
import ivar.hogblom.crmbackend.crm.repository.reseller.ResellerEventRepository;
import ivar.hogblom.crmbackend.util.ResellerDiffUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequireCrmDatabase
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "crmTransactionManager")
public class ResellerEventServiceImpl implements ResellerEventService {

    private final ResellerEventRepository resellerEventRepository;
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
        resellerEventRepository.save(toEntity(r, ResellerEventType.SKAPAT, CREATED + r));
    }

    // ----------------------------- UPDATE -----------------------------
    @Override
    
    public void logResellerUpdated(Reseller newR, List<String> diffs) {
        if (diffs == null || diffs.isEmpty() || newR == null) return;

        resellerEventRepository.save(
                toEntity(newR, ResellerEventType.UPPDATERAT, String.join("\n• ", diffs))
        );
    }

    // ----------------------------- PAUSE ------------------------------
    @Override
    public void logResellerPaused(Reseller r) {
        resellerEventRepository.save(toEntity(r, ResellerEventType.PAUSAT, PAUSED));
    }

    // ----------------------------- REACTIVATE -------------------------
    @Override
    public void logResellerReactivated(Reseller r) {
        resellerEventRepository.save(toEntity(r, ResellerEventType.ÅTERAKTIVERAT, REACTIVATED));
    }

    // ----------------------------- SUPPORT NOTE ------------------------
    @Override
    public void logResellerSupportNote(Reseller r, String note) {
        resellerEventRepository.save(toEntity(r, ResellerEventType.SUPPORT_ANTECKNING, note));
    }

    // ----------------------------- DELETE ------------------------------
    @Override
    public void logResellerDeleted(Reseller r) {
        resellerEventRepository.save(
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
    public List<ResellerEventDto> getEventsForReseller(UUID id) {
        return resellerEventRepository.findByResellerIdOrderByEventTsDesc(id)
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

    // -----------------------------------------------------
    // 🔴 DELETE SINGLE EVENT
    // -----------------------------------------------------
    @Override
    
    public void deleteEvent(UUID eventId) {

        if (!resellerEventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Reseller event not found: " + eventId);
        }

        resellerEventRepository.deleteById(eventId);
    }

    // -----------------------------------------------------
    // 🔴 DELETE ALL EVENTS FOR A RESELLER
    // -----------------------------------------------------
    @Override
    
    public void deleteAllEventsForReseller(UUID resellerId) {
        resellerEventRepository.deleteAllByResellerId(resellerId);
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
