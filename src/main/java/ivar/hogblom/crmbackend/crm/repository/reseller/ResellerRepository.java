package ivar.hogblom.crmbackend.crm.repository.reseller;

import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResellerRepository extends JpaRepository<Reseller, UUID> {

    Reseller findByName(String name);
    Reseller findByOrgNo(String orgNo);
    boolean existsByOrgNo(String orgNo);
    boolean existsByName(String name);

}
