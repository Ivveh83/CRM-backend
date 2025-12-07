package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SubscriptionDiffUtil {

    public List<String> diff(Subscription oldS, Subscription newS) {
        List<String> changes = new ArrayList<>();

        if (oldS == null || newS == null) {
            changes.add("Kunde inte jämföra abonnemangen (null-värden).");
            return changes;
        }

        /* ============================================================
           🔹 1. Jämför alla enkla fält
        ============================================================ */

        compare("Namn", oldS.getName(), newS.getName(), changes);
        compare("Kategori", oldS.getCategory(), newS.getCategory(), changes);
        compare("Beskrivning", oldS.getDescription(), newS.getDescription(), changes);
        compare("Servicenivå", oldS.getServiceLevel(), newS.getServiceLevel(), changes);
        compare("Pris per månad", oldS.getPricePerMonth(), newS.getPricePerMonth(), changes);
        compare("Kontraktslängd (mån)", oldS.getContractLength(), newS.getContractLength(), changes);
        compare("Förnyelseperiod", oldS.getRenewalPeriod(), newS.getRenewalPeriod(), changes);
        compare("Supportkontakt", oldS.getSupportContact(), newS.getSupportContact(), changes);
        compare("Aktiv", oldS.getActive(), newS.getActive(), changes);
        compare("Noteringar", oldS.getNotes(), newS.getNotes(), changes);

        return changes;
    }

    /* ============================================================
       🔧 Utility-method
    ============================================================ */
    private <T> void compare(String label, T oldValue, T newValue, List<String> changes) {
        if (!Objects.equals(oldValue, newValue)) {

            String oldStr = String.valueOf(oldValue);  // "null" om oldValue == null
            String newStr = String.valueOf(newValue);  // "null" om newValue == null

            String oldVal = (oldStr.equals("null") || oldStr.isEmpty()) ? "N/A" : oldStr;
            String newVal = (newStr.equals("null") || newStr.isEmpty()) ? "N/A" : newStr;

            changes.add(label + ": " + oldVal + " → " + newVal);
        }
    }


}
