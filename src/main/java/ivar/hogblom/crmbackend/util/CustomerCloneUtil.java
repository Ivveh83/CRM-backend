package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.customer.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerCloneUtil {

    public Customer clone(Customer c) {
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
                .build();
    }
}
