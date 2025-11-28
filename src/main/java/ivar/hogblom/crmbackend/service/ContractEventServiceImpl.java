package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.ContractEvent;
import ivar.hogblom.crmbackend.entity.ContractEventType;
import ivar.hogblom.crmbackend.repository.ContractEventRepository;
import ivar.hogblom.crmbackend.util.ContractDiffUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractEventServiceImpl implements ContractEventService {

    private final ContractEventRepository eventRepository;

    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getActor() {
        Authentication authentication = getAuth();
        return authentication != null ? authentication.getName() : "System";
    }

    @Transactional
    @Override
    public void handleContractUpdate(Contract oldC, Contract newC) {

        // Kör diff
        List<String> changes = ContractDiffUtil.diff(oldC, newC);

        if (changes.isEmpty()) {
            return; // inget att logga
        }

        String details = String.join(" • ", changes);

        ContractEvent event = ContractEvent.builder()
                .contractId(newC.getId())
                .customerOrgNo(newC.getCustomer().getOrgNo())
                .eventType(ContractEventType.UPPDATERAT)
                .detail(details)
                .eventTs(LocalDateTime.now())
                .actor(getActor())  // Hämtar namnet på den inloggade ur SecurityContextHolder
                .build();

        eventRepository.save(event);
    }

    @Transactional
    @Override
    public void handleContractActiveUpdate(Contract newC, boolean newActive, String details) {

        if (details == null || details.isBlank()) {
            details = newActive
                    ? "Kontraktet återaktiverades."
                    : "Kontraktet pausades.";
        }

        ContractEventType eventType = newActive
        ? ContractEventType.ÅTERAKTIVERAT
                : ContractEventType.PAUSAT;

        ContractEvent event = ContractEvent.builder()
                .contractId(newC.getId())
                .customerOrgNo(newC.getCustomer().getOrgNo())
                .eventType(eventType)
                .detail(details)
                .eventTs(LocalDateTime.now())
                .actor(getActor())  // Hämtar namnet på den inloggade ur SecurityContextHolder
                .build();

        eventRepository.save(event);
    }
}
