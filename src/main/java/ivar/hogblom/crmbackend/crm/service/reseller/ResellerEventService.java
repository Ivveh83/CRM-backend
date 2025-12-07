package ivar.hogblom.crmbackend.crm.service.reseller;

import ivar.hogblom.crmbackend.dto.reseller.ResellerEventDto;
import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;

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

    void deleteEvent(UUID eventId);

    void deleteAllEventsForReseller(UUID resellerId);
}
