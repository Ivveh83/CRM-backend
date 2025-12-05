package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.contract.Contract;
import ivar.hogblom.crmbackend.entity.customer.Customer;
import ivar.hogblom.crmbackend.entity.reseller.Reseller;
import ivar.hogblom.crmbackend.entity.subscription.Subscription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractCloneUtil {

    public static Contract cloneContract(Contract c) {
        if (c == null) return null;

        return Contract.builder()
                .id(c.getId())
                .status(c.isStatus())
                .active(c.isActive())
                .contractDate(c.getContractDate())
                .contractLengthMonths(c.getContractLengthMonths())
                .totalPricePerMonth(c.getTotalPricePerMonth())
                .dueDate(c.getDueDate())

                .renewalDates(
                        c.getRenewalDates() != null
                                ? List.copyOf(c.getRenewalDates())
                                : null
                )

                .comment(c.getComment())

                // Deep clone relations
                .customer(cloneCustomer(c.getCustomer()))
                .resellers(cloneResellers(c.getResellers()))
                .subscriptions(cloneSubscriptions(c.getSubscriptions()))

                .build();
    }

    /* ===================== CUSTOMER ===================== */

    private static Customer cloneCustomer(Customer c) {
        if (c == null) return null;

        return Customer.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .orgNo(c.getOrgNo())
                .contactName(c.getContactName())
                .contactEmail(c.getContactEmail())
                .contactPhone(c.getContactPhone())
                .address(c.getAddress())
                .city(c.getCity())
                .zipCode(c.getZipCode())
                .country(c.getCountry())
                .industry(c.getIndustry())
                .customerType(c.getCustomerType())
                .createdAt(c.getCreatedAt())
                .notes(c.getNotes())
                // ⚠ Viktigt: kopiera INTE c.getContracts(), annars recursion
                .build();
    }

    /* ===================== RESELLERS ===================== */

    private static List<Reseller> cloneResellers(List<Reseller> resellers) {
        if (resellers == null) return null;

        return resellers.stream()
                .map(r -> Reseller.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .orgNo(r.getOrgNo())
                        .active(r.isActive())
                        .address(r.getAddress())
                        .contactEmail(r.getContactEmail())
                        .contactTelephone(r.getContactTelephone())
                        .invoiceReference(r.getInvoiceReference())
                        .createdAt(r.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /* ===================== SUBSCRIPTIONS ===================== */

    private static List<Subscription> cloneSubscriptions(List<Subscription> subs) {
        if (subs == null) return null;

        return subs.stream()
                .map(s -> Subscription.builder()
                        .id(s.getId())
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
                        .build()
                )
                .collect(Collectors.toList());
    }
}
