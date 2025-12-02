package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.ResellerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResellerEventRepository extends JpaRepository<ResellerEvent, UUID> {

    List<ResellerEvent> findByResellerIdOrderByEventTsDesc(UUID resellerId);
}
