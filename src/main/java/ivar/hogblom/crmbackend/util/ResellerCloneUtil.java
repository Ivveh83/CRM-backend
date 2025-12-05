package ivar.hogblom.crmbackend.util;

import ivar.hogblom.crmbackend.entity.reseller.Reseller;
import org.springframework.stereotype.Component;

@Component
public class ResellerCloneUtil {

    public Reseller clone(Reseller r) {
        if (r == null) return null;

        return Reseller.builder()
                .id(r.getId())
                .name(r.getName())
                .orgNo(r.getOrgNo())
                .address(r.getAddress())
                .contactEmail(r.getContactEmail())
                .contactTelephone(r.getContactTelephone())
                .invoiceReference(r.getInvoiceReference())
                .active(r.isActive())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

