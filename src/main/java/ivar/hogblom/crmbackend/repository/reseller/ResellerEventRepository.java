package ivar.hogblom.crmbackend.repository.reseller;

import ivar.hogblom.crmbackend.entity.reseller.ResellerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResellerEventRepository extends JpaRepository<ResellerEvent, UUID> {

    List<ResellerEvent> findByResellerIdOrderByEventTsDesc(UUID resellerId);

    void deleteAllByResellerId(UUID resellerId);
}
