package ivar.hogblom.crmbackend.crm.repository.lookup;

import ivar.hogblom.crmbackend.crm.entity.lookup.LookupValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupValueRepository extends JpaRepository<LookupValue, String> {
    List<LookupValue> findByTypeOrderBySortOrderAsc(String type);
    List<LookupValue> findByTypeAndActiveTrueOrderBySortOrderAsc(String type);
    List<LookupValue> findByType(String type);
    boolean existsByTypeAndValue(String type, String value);
    boolean existsByTypeAndLabelAndIdNot(String type, String label, String id);

}

