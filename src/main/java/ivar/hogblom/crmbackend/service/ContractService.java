package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ContractActiveUpdateDto;
import ivar.hogblom.crmbackend.dto.ContractResponseDto;
import ivar.hogblom.crmbackend.dto.ContractRequestDto;

import java.util.List;
import java.util.UUID;

public interface ContractService {

    List<ContractResponseDto> findAll();
    ContractResponseDto findById(UUID id);
    void createContract(ContractRequestDto request);
    void updateContract(UUID id, ContractRequestDto request);
    void updateContractActive(UUID id, ContractActiveUpdateDto dto);
    void deleteContract(UUID id);
}
