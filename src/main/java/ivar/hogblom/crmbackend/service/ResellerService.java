package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.ResellerResponseDto;

import java.util.List;
import java.util.UUID;

public interface ResellerService {

    List<ResellerResponseDto> findAllResellers();
    List<ResellerForContractComponentsDto> findAllResellersForContractComponents();
    ResellerResponseDto findById(UUID id);
    void createReseller(ResellerRequestDto request);
    void updateReseller(UUID id, ResellerRequestDto request);
    void deleteReseller(UUID id);
    void updateResellerActive(UUID id, boolean active);
}
