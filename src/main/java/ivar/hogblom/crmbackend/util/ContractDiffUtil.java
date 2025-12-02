package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ContractDiffUtil {

    public static List<String> diff(Contract oldC, Contract newC) {
        List<String> changes = new ArrayList<>();

        if (oldC == null || newC == null) {
            changes.add("Kunde inte jämföra kontrakten (null-värden).");
            return changes;
        }

        /* ============================================================
           🔹 1. Boolean fields
        ============================================================ */

        if (oldC.isStatus() != newC.isStatus()) {
            String before = oldC.isStatus() ? "Öppet" : "Stängt";
            String after = newC.isStatus() ? "Öppet" : "Stängt";
            changes.add("Status ändrad: " + before + " → " + after);
        }

        /* ============================================================
           🔹 2. Enkla fält (LocalDate, Integer, String)
        ============================================================ */

        compareField(oldC.getContractDate(), newC.getContractDate(), "Kontraktsdatum ändrat", changes);
        compareField(oldC.getDueDate(), newC.getDueDate(), "Förfallodatum ändrat", changes);
        compareField(oldC.getContractLengthMonths(), newC.getContractLengthMonths(), "Kontraktslängd (mån) ändrad", changes);
        compareField(oldC.getComment(), newC.getComment(), "Kommentar ändrad", changes);
        compareField(oldC.getTotalPricePerMonth(), newC.getTotalPricePerMonth(), "Totalt pris ändrat", changes);

        /* ============================================================
           🔹 3. Renewal-datum (listor med LocalDate)
        ============================================================ */

        if (!Objects.equals(oldC.getRenewalDates(), newC.getRenewalDates())) {
            changes.add("Förnyelsedatum ändrades: "
                    + oldC.getRenewalDates() + " → " + newC.getRenewalDates());
        }

        /* ============================================================
           🔹 4. Customer (endast attribut, inte deep-contracts)
        ============================================================ */

        if (!Objects.equals(oldC.getCustomer().getId(), newC.getCustomer().getId())) {
            changes.add("Kund ändrad: "
                    + oldC.getCustomer().getCompanyName()
                    + " → "
                    + newC.getCustomer().getCompanyName());
        }

        /* ============================================================
           🔹 5. Resellers (list-diff)
        ============================================================ */

        diffSimpleEntityList(
                oldC.getResellers(),
                newC.getResellers(),
                Reseller::getName,
                "Återförsäljare ",
                changes
        );

        /* ============================================================
           🔹 6. Subscriptions (list-diff)
        ============================================================ */

        diffSimpleEntityList(
                oldC.getSubscriptions(),
                newC.getSubscriptions(),
                Subscription::getName,
                "Abonnemang ",
                changes
        );

        return changes;
    }

    /* ============================================================
       🔧 Utility: Jämför generisk property
    ============================================================ */

    private static <T> void compareField(T oldValue, T newValue, String fieldName, List<String> changes) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(fieldName + ": " + oldValue + " → " + newValue);
        }
    }

    /* ============================================================
       🔧 Utility: diff av listor (resellers, subscriptions)
    ============================================================ */

    private static <T> void diffSimpleEntityList(
            List<T> oldList,
            List<T> newList,
            java.util.function.Function<T, String> nameExtractor,
            String entityLabel,
            List<String> changes
    ) {

        Set<String> oldNames = oldList.stream().map(nameExtractor).collect(Collectors.toSet());
        Set<String> newNames = newList.stream().map(nameExtractor).collect(Collectors.toSet());

        // Added
        for (String added : newNames) {
            if (!oldNames.contains(added)) {
                changes.add(entityLabel + " tillagd: " + added);
            }
        }

        // Removed
        for (String removed : oldNames) {
            if (!newNames.contains(removed)) {
                changes.add(entityLabel + " borttagen: " + removed);
            }
        }
    }
}
