package ivar.hogblom.crmbackend.service.lookup;

import ivar.hogblom.crmbackend.dto.lookup.LookupSortUpdateDto;
import ivar.hogblom.crmbackend.dto.lookup.LookupValueCreateDto;
import ivar.hogblom.crmbackend.dto.lookup.LookupValueResponseDto;
import ivar.hogblom.crmbackend.dto.lookup.LookupValueUpdateDto;

import java.util.List;

public interface LookupValueService {
    void create(LookupValueCreateDto dto);
    void update(String id, LookupValueUpdateDto dto);
    List<LookupValueResponseDto> getAllByTypeAndActive(String type, boolean onlyActive);
    List<LookupValueResponseDto> getAllByType(String type);
    void updateActive(String id, boolean active);
    void updateSortOrder(String type, List<LookupSortUpdateDto> updates);

}

