package ivar.hogblom.crmbackend.crm.repository.reseller;

import ivar.hogblom.crmbackend.crm.entity.reseller.ResellerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResellerEventRepository extends JpaRepository<ResellerEvent, UUID> {

    List<ResellerEvent> findByResellerIdOrderByEventTsDesc(UUID resellerId);

    void deleteAllByResellerId(UUID resellerId);
}
