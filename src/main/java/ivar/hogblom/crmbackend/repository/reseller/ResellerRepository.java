package ivar.hogblom.crmbackend.repository.reseller;

import ivar.hogblom.crmbackend.entity.reseller.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResellerRepository extends JpaRepository<Reseller, UUID> {

    Reseller findByName(String name);
    Reseller findByOrgNo(String orgNo);
    boolean existsByOrgNo(String orgNo);
    boolean existsByName(String name);

}
