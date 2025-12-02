package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.Reseller;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ResellerDiffUtil {

    public List<String> diff(Reseller oldR, Reseller newR) {
        List<String> diffs = new ArrayList<>();

        if (oldR == null || newR == null) {
            diffs.add("Kunde inte jämföra återförsäljardata (null-värden).");
            return diffs;
        }

        compare("Namn", oldR.getName(), newR.getName(), diffs);
        compare("Organisationsnummer", oldR.getOrgNo(), newR.getOrgNo(), diffs);
        compare("Adress", oldR.getAddress(), newR.getAddress(), diffs);
        compare("E-post", oldR.getContactEmail(), newR.getContactEmail(), diffs);
        compare("Telefon", oldR.getContactTelephone(), newR.getContactTelephone(), diffs);
        compare("Fakturareferens", oldR.getInvoiceReference(), newR.getInvoiceReference(), diffs);
        compare("Aktiv", oldR.isActive(), newR.isActive(), diffs);

        return diffs;
    }

    private void compare(String label, Object oldVal, Object newVal, List<String> diffs) {
        if (!Objects.equals(oldVal, newVal)) {
            diffs.add(label + ": " + oldVal + " → " + newVal);
        }
    }
}
