package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CustomerDiffUtil {

    public List<String> diff(Customer oldC, Customer newC) {
        List<String> diffs = new ArrayList<>();

        if (oldC == null || newC == null) {
            diffs.add("Kunde inte jämföra kunddata (null-värden).");
            return diffs;
        }

        compare("Företagsnamn", oldC.getCompanyName(), newC.getCompanyName(), diffs);
        compare("Organisationsnummer", oldC.getOrgNo(), newC.getOrgNo(), diffs);
        compare("Kontaktperson", oldC.getContactName(), newC.getContactName(), diffs);
        compare("E-post", oldC.getContactEmail(), newC.getContactEmail(), diffs);
        compare("Telefon", oldC.getContactPhone(), newC.getContactPhone(), diffs);
        compare("Adress", oldC.getAddress(), newC.getAddress(), diffs);
        compare("Stad", oldC.getCity(), newC.getCity(), diffs);
        compare("Postnummer", oldC.getZipCode(), newC.getZipCode(), diffs);
        compare("Land", oldC.getCountry(), newC.getCountry(), diffs);
        compare("Bransch", oldC.getIndustry(), newC.getIndustry(), diffs);
        compare("Kundtyp", oldC.getCustomerType(), newC.getCustomerType(), diffs);
        compare("Anteckningar", oldC.getNotes(), newC.getNotes(), diffs);

        return diffs;
    }

    private void compare(String label, Object oldVal, Object newVal, List<String> diffs) {
        if (!Objects.equals(oldVal, newVal)) {
            diffs.add(label + ": " + oldVal + " → " + newVal);
        }
    }
}

