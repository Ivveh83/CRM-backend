package ivar.hogblom.crmbackend.repository;

import ivar.hogblom.crmbackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Customer findByCompanyName(String companyName);
    Customer findByOrgNo(String orgNo);
    boolean existsByCompanyName(String companyName);
    boolean existsByOrgNo(String orgNo);
}
