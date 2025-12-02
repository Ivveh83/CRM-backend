package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ResellerEventDto;
import ivar.hogblom.crmbackend.entity.Reseller;

import java.util.List;
import java.util.UUID;

public interface ResellerEventService {

    void logResellerCreated(Reseller reseller);

    void logResellerUpdated(Reseller newR, List<String> diffs);

    void logResellerPaused(Reseller reseller);

    void logResellerReactivated(Reseller reseller);

    void logResellerDeleted(Reseller reseller);

    void logResellerSupportNote(Reseller reseller, String note);

    List<ResellerEventDto> getEventsForReseller(UUID resellerId);
}
