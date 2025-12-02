package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ContractEventDto;
import ivar.hogblom.crmbackend.dto.ContractRenewalDto;
import ivar.hogblom.crmbackend.entity.*;
import ivar.hogblom.crmbackend.repository.ContractEventRepository;
import ivar.hogblom.crmbackend.repository.ContractRepository;
import ivar.hogblom.crmbackend.service.dto.ContractPriceChange;
import ivar.hogblom.crmbackend.util.ContractDiffUtil;
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
public class ContractEventServiceImpl implements ContractEventService {

    private final String CREATED = "Kontraktet skapades.";
    private final String DELETED = "Kontraktet raderades.";

    private final ContractEventRepository eventRepository;
    private final ContractRepository contractRepository;


    // -----------------------------------------------------
    // 🔵 CREATED
    // -----------------------------------------------------
    @Override
    public void logContractCreated(Contract newC) {

        ContractEvent event = toEntity(
                newC,
                ContractEventType.SKAPAT,
                CREATED
        );

        eventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 UPDATED
    // -----------------------------------------------------
    @Transactional
    @Override
    public void logContractUpdate(Contract oldC, Contract newC) {

        List<String> changes = ContractDiffUtil.diff(oldC, newC);
        if (changes.isEmpty()) {
            return;
        }

        String details = String.join("\n• ", changes);

        ContractEvent event = toEntity(
                newC,
                ContractEventType.UPPDATERAT,
                details
        );

        eventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 ACTIVE / PAUSE
    // -----------------------------------------------------
    @Transactional
    @Override
    public void logContractActiveUpdate(Contract newC, boolean newActive, String details) {

        if (details == null || details.isBlank()) {
            details = newActive
                    ? "Kontraktet återaktiverades."
                    : "Kontraktet pausades.";
        }

        ContractEventType eventType = newActive
                ? ContractEventType.ÅTERAKTIVERAT
                : ContractEventType.PAUSAT;

        ContractEvent event = toEntity(
                newC,
                eventType,
                details
        );

        eventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 RENEWAL
    // -----------------------------------------------------
    @Override
    public void logContractRenewal(Contract oldC, ContractRenewalDto dto) {

        String details = "Kontraktet förnyades. Nytt förfallodatum: " + dto.dueDate();

        ContractEvent event = toEntity(
                oldC,
                ContractEventType.FÖRNYAT,
                details
        );

        eventRepository.save(event);
    }


    // -----------------------------------------------------
    // 🔵 DELETED
    // -----------------------------------------------------
    @Override
    public void logContractDeleted(UUID oldCId, String oldCCustomerOrgNo) {

        ContractEvent event = ContractEvent.builder()
                .contractId(oldCId)
                .customerOrgNo(oldCCustomerOrgNo)
                .eventType(ContractEventType.RADERAT)
                .detail(DELETED)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();

        eventRepository.save(event);
    }

    // -----------------------------------------------------
    // 🔵 LOG SUBSCRIPTION ACTIVE UPDATE
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logSubscriptionActiveUpdate(Subscription s) {
        List<Contract> contracts = contractRepository.findAllBySubscriptions_Id(s.getId());
        final String DETAIL = "Abonnemanget " + s.getName() +
                (s.getActive() ? " återaktiverades" : " inaktiverades");
        for (Contract c : contracts) {
            ContractEvent event = toEntity(c, ContractEventType.UPPDATERAT, DETAIL);
            eventRepository.save(event);
        }
    }

    // -----------------------------------------------------
    // 🔵 LOG SUBSCRIPTION UPDATE
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logSubscriptionUpdate(
            Subscription oldS,
            Subscription newS,
            List<String> subscriptionDiffs,
            List<ContractPriceChange> priceChanges,
            List<Contract> allAffectedContracts
    ) {
        // Om absolut inget ändrats → inget event
        if ((subscriptionDiffs == null || subscriptionDiffs.isEmpty()) &&
                (priceChanges == null || priceChanges.isEmpty())) {
            return;
        }

        // Fall A: Prisändringar finns → loopa priceChanges
        if (priceChanges != null && !priceChanges.isEmpty()) {
            for (ContractPriceChange pc : priceChanges) {

                Contract contract = pc.getContract();

                StringBuilder sb = new StringBuilder();
                sb.append("Abonnemanget ")
                        .append(newS.getName())
                        .append(" uppdaterades.\n");

                // 🔹 Abonnemangs-diff
                if (subscriptionDiffs != null && !subscriptionDiffs.isEmpty()) {
                    sb.append("Ändringar i abonnemanget:\n");
                    for (String d : subscriptionDiffs) {
                        sb.append(" ● ").append(d).append("\n");
                    }
                }

                // 🔹 Prisändring (alltid finns här)
                sb.append("● Kontraktets totalpris ändrades: ")
                        .append(pc.getOldTotalPrice())
                        .append(" → ")
                        .append(pc.getNewTotalPrice())
                        .append("\n");

                ContractEvent event = toEntity(
                        contract,
                        ContractEventType.UPPDATERAT,
                        sb.toString().trim()
                );

                eventRepository.save(event);
            }

            return;
        }

        // Fall B: INGA prisändringar → men abonnemang ändrades
        // Logga ändringarna för ALLA kontrakt
        for (Contract contract : allAffectedContracts) {

            StringBuilder sb = new StringBuilder();
            sb.append("Abonnemanget ")
                    .append(newS.getName())
                    .append(" uppdaterades.\n");

            sb.append("Ändringar i abonnemanget:\n");
            for (String d : subscriptionDiffs) {
                sb.append(" ● ").append(d).append("\n");
            }

            ContractEvent event = toEntity(
                    contract,
                    ContractEventType.UPPDATERAT,
                    sb.toString().trim()
            );

            eventRepository.save(event);
        }
    }

    // -----------------------------------------------------
    // 🔵 LOG SUBSCRIPTION DELETE
    // -----------------------------------------------------

    @Transactional
    public void logSubscriptionDeleted(Subscription oldS,
                                       List<ContractPriceChange> priceChanges,
                                       List<Contract> contracts) {
        for (ContractPriceChange pc : priceChanges) {
            Contract c = pc.getContract();

            StringBuilder sb = new StringBuilder();
            sb.append("Abonnemanget '")
                    .append(oldS.getName())
                    .append("' raderades från systemet.\n");
            sb.append("Abonnemangets uppgifter: \n");
            sb.append(" ● Id: ").append(oldS.getId()).append("\n")
                    .append(" ● Kategori: ").append(oldS.getCategory()).append("\n")
                    .append(" ● Beskrivning: ").append(oldS.getDescription()).append("\n")
                    .append(" ● Servicenivå: ").append(oldS.getServiceLevel()).append("\n")
                    .append(" ● Pris per månad: ").append(oldS.getPricePerMonth()).append("\n")
                    .append(" ● Kontraktslängd: ").append(oldS.getContractLength()).append("\n")
                    .append(" ● Förnyelseperiod: ").append(oldS.getRenewalPeriod()).append("\n")
                    .append(" ● Supportkontakt: ").append(oldS.getSupportContact()).append("\n")
                    .append(" ● Skapad: ").append(oldS.getCreatedAt()).append("\n")
                    .append(" ● Anteckningar: ").append(oldS.getNotes());

            sb.append("Kontraktets pris räknades om: ")
                    .append(pc.getOldTotalPrice())
                    .append(" → ")
                    .append(pc.getNewTotalPrice())
                    .append("\n");

            ContractEvent event = toEntity(
                    c,
                    ContractEventType.UPPDATERAT,
                    sb.toString().trim()
            );

            eventRepository.save(event);
        }
    }

    // -----------------------------------------------------
    // 🔵 LOG CUSTOMER UPDATE
    // -----------------------------------------------------

    @Override
    @Transactional
    public void logCustomerUpdate(
            Customer oldC,
            Customer newC,
            List<String> diffs,
            List<Contract> contracts
    ) {

        for (Contract c : contracts) {

            StringBuilder sb = new StringBuilder();

            sb.append("Kunduppgifter uppdaterades för kunden ")
                    .append(newC.getCompanyName())
                    .append(":\n");

            for (String d : diffs) {
                sb.append(" ● ").append(d).append("\n");
            }

            ContractEvent event = toEntity(
                    c,
                    ContractEventType.UPPDATERAT,
                    sb.toString().trim()
            );

            eventRepository.save(event);
        }
    }

    // -----------------------------------------------------
    // 🔵 LOG SUBSCRIPTION UPDATE
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logResellerUpdate(
            Reseller oldR,
            Reseller newR,
            List<String> diffs,
            List<Contract> contracts
    ) {

        for (Contract c : contracts) {

            StringBuilder sb = new StringBuilder();

            sb.append("Återförsäljare uppdaterades: ")
                    .append(newR.getName())
                    .append("\n");

            for (String d : diffs) {
                sb.append(" ● ").append(d).append("\n");
            }

            ContractEvent event = toEntity(
                    c,
                    ContractEventType.UPPDATERAT,
                    sb.toString().trim()
            );

            eventRepository.save(event);
        }
    }

// -----------------------------------------------------
// 🔵 LOG RESELLER ACTIVE UPDATE
// -----------------------------------------------------
    @Override
    @Transactional
    public void logResellerActiveUpdate(Reseller r) {

        List<Contract> contracts = contractRepository.findAllByResellers_Id(r.getId());

        final String DETAIL = "Återförsäljaren " + r.getName() +
                (r.isActive()
                        ? " återaktiverades"
                        : " inaktiverades");

        for (Contract c : contracts) {

            ContractEvent event = toEntity(
                    c,
                    ContractEventType.UPPDATERAT,
                    DETAIL
            );

            eventRepository.save(event);
        }
    }


    // -----------------------------------------------------
    // 🔵 LOG RESELLER DELETE
    // -----------------------------------------------------
    @Override
    @Transactional
    public void logResellerDeleted(Reseller deleted, List<Contract> contracts) {

        for (Contract c : contracts) {

            StringBuilder sb = new StringBuilder();

            sb.append("Återförsäljare raderades: ")
                    .append(deleted.getName())
                    .append("\n\n");

            sb.append("Uppgifter före radering:\n")
                    .append(" ● Namn: ").append(deleted.getName()).append("\n")
                    .append(" ● OrgNr: ").append(deleted.getOrgNo()).append("\n")
                    .append(" ● E-post: ").append(deleted.getContactEmail()).append("\n")
                    .append(" ● Telefon: ").append(deleted.getContactTelephone()).append("\n")
                    .append(" ● Adress: ").append(deleted.getAddress()).append("\n")
                    .append(" ● Fakturareferens: ").append(deleted.getInvoiceReference()).append("\n");

            ContractEvent ev = toEntity(
                    c,
                    ContractEventType.UPPDATERAT,
                    sb.toString().trim()
            );

            eventRepository.save(ev);
        }
    }



    // -----------------------------------------------------
    // 🔵 GET ALL CONTRACT EVENTS FOR A SINGLE CONTRACT
    // -----------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ContractEventDto> getEventsForContract(UUID contractId) {

        return eventRepository.findByContractIdOrderByEventTsDesc(contractId)
                .stream()
                .map(event -> ContractEventDto.builder()
                        .id(event.getId())
                        .eventType(event.getEventType().name())
                        .detail(event.getDetail())
                        .eventTs(event.getEventTs())
                        .actor(event.getActor())
                        .build()
                )
                .toList();
    }



    // -----------------------------------------------------
    // 🔵 HELPER METHODS
    // -----------------------------------------------------

    private static Authentication fetchAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static String fetchActor() {
        Authentication authentication = fetchAuth();
        return authentication != null ? authentication.getName() : "System";
    }

    public static ContractEvent toEntity(
            Contract contract,
            ContractEventType eventType,
            String detail
    ) {
        return ContractEvent.builder()
                .contractId(contract.getId())
                .customerOrgNo(contract.getCustomer().getOrgNo())
                .eventType(eventType)
                .detail(detail)
                .eventTs(LocalDateTime.now())
                .actor(fetchActor())
                .build();
    }
}
