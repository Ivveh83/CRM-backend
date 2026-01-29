package ivar.hogblom.crmbackend.crm.repository.reseller;

import ivar.hogblom.crmbackend.crm.entity.reseller.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResellerRepository extends JpaRepository<Reseller, UUID> {

    boolean existsByOrgNo(String orgNo);
    boolean existsByName(String name);

}
