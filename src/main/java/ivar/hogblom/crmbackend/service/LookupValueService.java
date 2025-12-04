package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.LookupSortUpdateDto;
import ivar.hogblom.crmbackend.dto.LookupValueCreateDto;
import ivar.hogblom.crmbackend.dto.LookupValueResponseDto;
import ivar.hogblom.crmbackend.dto.LookupValueUpdateDto;

import java.util.List;

public interface LookupValueService {
    void create(LookupValueCreateDto dto);
    void update(String id, LookupValueUpdateDto dto);
    List<LookupValueResponseDto> getAllByTypeAndActive(String type, boolean onlyActive);
    List<LookupValueResponseDto> getAllByType(String type);
    void updateActive(String id, boolean active);
    void updateSortOrder(String type, List<LookupSortUpdateDto> updates);

}

